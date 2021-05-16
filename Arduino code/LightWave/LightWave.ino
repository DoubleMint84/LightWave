//----------------------НАСТРОЙКИ-----------------------
#define COLOR_DEBTH 2
#define maxArrSize 28
#define kolArrMenu 3
#define kolArrSettings 4
#define clock_adr 0x68
#define al_kol 7 // количество будильников - по дням недели
#define PARSE_AMOUNT 6
#define NUMPIXELS 60
#define dawn_Time 2 //в минутах

#define DEBUG 0 // 1 - дебаг в сериал-порт
//-------------------КОНЕЦ-НАСТРОЕК---------------------

//------------------------ПИНЫ--------------------------
// кнопка
#define butPin 2

// подключение ленты
#define ledPin 3

// Дисплей TM1637
#define CLK_tm 7
#define DIO 6

// Адрес DS3231
#define clock_adr 0x68

// HC-06
#define hc_TX 4
#define hc_RX 5

// SD - порт
#define sd_pin 10
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
#include "GyverTimer.h" 
//------------------КОНЕЦ-БИБЛИОТЕК---------------------

//---------------------СТРУКТУРЫ------------------------
struct oneAlarm {
  int8_t hour;
  int8_t minute;
  int8_t dawnHour;
  int8_t dawnMin;
  bool isActive;
};
//-------------------КОНЕЦ-СТРУКТУР---------------------

microLED<NUMPIXELS, ledPin, MLED_NO_CLOCK, LED_WS2812, ORDER_GRB, CLI_AVER> pixels;
RTC_DS3231 rtc;
GyverTM1637 disp(CLK_tm, DIO);
GButton but(butPin);
SoftwareSerial blueTooth(hc_TX, hc_RX);
DateTime t_now, t_prev;
oneAlarm alarms[al_kol];
GTimer effectTimer(MS, 30), dotsTimer(MS, 100), randomTimer(MS, 15000), breathTimer(MS, 30); // таймер прорисовки эффектов

long dawnTime = dawn_Time;
const long dawnStep = (long(dawnTime) * long(60000)) / 1400;
int brightTemp = 0;
int dawnTemp = 900;
bool ledActive = false;

int alarmRaise = -1;

byte change_time = 0;

int intData[PARSE_AMOUNT];
byte index;
String string_convert = "";
boolean recievedFlag = false, getStarted = false;

bool is_breath = false;
int ledEffect = -1;
int kelvinTemp = 3400;
int ledBrightness = 255;
byte ledParameter = 0;

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
  effectTimer.start();
  dotsTimer.start();
  randomTimer.start();
  breathTimer.start();
  randomSeed(A0);
}

void loop() {
  parsing(); // парсинг входящих данных
  command_parse(); // обработка команд
  buttonTick();
  t_now = rtc.now();
  alarmTick();
  dawnTick();
  effectTick();
  if ((t_now.minute() != t_prev.minute()) and (change_time == 0)) {
    disp.displayClock(byte(t_now.hour()), byte(t_now.minute()));
    t_prev = t_now;
  }
}
