//----------------------НАСТРОЙКИ-----------------------
#define ORDER_RGB       // порядок цветов ORDER_GRB / ORDER_RGB / ORDER_BRG

#define COLOR_DEBTH 2
#define maxArrSize 28
#define kolArrMenu 3
#define kolArrSettings 4
#define clock_adr 0x68
#define al_kol 7 // количество будильников - по дням недели
#define PARSE_AMOUNT 6
#define NUMPIXELS 15
#define dawnTime 2 //в минутах
#define ledBrightness 255

#define DEBUG 0 // 1 - дебаг в сериал-порт
//-------------------КОНЕЦ-НАСТРОЕК---------------------

//------------------------ПИНЫ--------------------------
// кнопка
#define butPin 2

// подключение ленты
#define ledPin 3

// Дисплей TM1637
#define CLK_tm 12
#define DIO 11

// Адрес DS3231
#define clock_adr 0x68

// HC-06
#define hc_TX 4
#define hc_RX 5

// SD - порт
#define sd_pin 9
//---------------------КОНЕЦ-ПИНОВ---------------------

//---------------------БИБЛИОТЕКИ-----------------------
#include "RTClib.h"
#include <GyverButton.h>
#include <Wire.h>
#include "GyverTM1637.h"
#include <SPI.h>
#include <SD.h>
#include <microLED.h>
#include <SoftwareSerial.h>
//------------------КОНЕЦ-БИБЛИОТЕК---------------------

//---------------------СТРУКТУРЫ------------------------
struct oneAlarm {
  int8_t hour;
  int8_t minute;
  int8_t dawnHour;
  int8_t dawnMin;
  bool isActive;
};

struct LedPreset {
  byte r;
  byte g;
  byte b;
};
//-------------------КОНЕЦ-СТРУКТУР---------------------

LEDdata leds[NUMPIXELS];  // буфер ленты типа LEDdata (размер зависит от COLOR_DEBTH)
microLED pixels(leds, NUMPIXELS, ledPin);
RTC_DS3231 rtc;
GyverTM1637 disp(CLK_tm, DIO);
GButton but(butPin);
SoftwareSerial blueTooth(hc_TX, hc_RX);

const long dawnStep = (long(dawnTime) * long(60000)) / 750;
unsigned long lightTime = 0;
int ledR = 0, ledG = 0, ledB = 0;
int value = 0, change = 0, alarmRaise = -1;
int intData[PARSE_AMOUNT];
byte level = 0, change_time = 0;
bool inMenu = false, dots = true, ledActive = false;
DateTime t_now, t_prev;
oneAlarm alarms[al_kol];
LedPreset ledPreset;
boolean recievedFlag = false, lamp = false;
boolean getStarted = false, blinkBuzz = false;
byte index;
String string_convert = "";
unsigned long timerAlarm;

void setup() {
#if (DEBUG == 1)
  Serial.begin(9600);
  Serial.println(F("///////////////////////////////////"));
  Serial.println(F("Starting up"));
  Serial.println(dawnStep);
#endif

  // ЛЕНТА
  pixels.setBrightness(ledBrightness);    // яркость (0-255)
  // яркость применяется при выводе .show() !
  pixels.setMaxCurrent(1600);
  pixels.setVoltage(5000);
  pixels.clear();   // очищает буфер
  // применяется при выводе .show() !
  pixels.show();

  // HC-06
  blueTooth.begin(9600);
  
  // DS3231
  rtc.begin();
  t_now = rtc.now();
  t_prev = t_now;

  // Дисплей TM1637
  disp.clear();
  disp.brightness(7);
  disp.displayClock(byte(t_now.hour()), byte(t_now.minute()));
  disp.point(true);
  
  // Кнопка
  but.setDebounce(50);        // настройка антидребезга (по умолчанию 80 мс)
  but.setTimeout(300);        // настройка таймаута на удержание (по умолчанию 500 мс)
  but.setClickTimeout(600);
  but.setType(LOW_PULL);
  but.setDirection(NORM_OPEN);

  // SD - порт
#if (DEBUG == 1)
  Serial.print("Initializing SD card...");
#endif
  if (!SD.begin(sd_pin)) {
#if (DEBUG == 1)
    Serial.println("SD initialization failed! Program stopped");
#endif
    while (1);
  }

  // Инициализация памяти и времени рассвета
  dataSdRead();
  calcDawn();
#if (DEBUG == 1)
  Serial.print("Initialization done. All systems clear.");
#endif
}

void loop() {
  parsing(); // парсинг входящих данных
  command_parse(); // обработка команд
  t_now = rtc.now();
  alarmTick();
  dawnTick();
  if ((t_now.minute() != t_prev.minute()) and (change_time == 0)) {
    disp.displayClock(byte(t_now.hour()), byte(t_now.minute()));
    t_prev = t_now;
  }
}