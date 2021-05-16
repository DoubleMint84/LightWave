// Рассвет и будильники

void alarmTick() {
  static uint32_t timerAlarm;
  if (alarmRaise == -1 and change_time == 0) {
    for (int i = 0; i < al_kol; i++) {
      if ((alarms[i].hour == t_now.hour()) and (alarms[i].minute == t_now.minute()) and (t_now.second() == 0) and alarms[i].isActive) {
        alarmRaise = i;
        timerAlarm = millis();
        break;
      }
    }
  }
  if (alarmRaise != -1) {
    if (millis() - timerAlarm >= 300000) { // если через 5 минут не была нажата кнопка, то выключаем ленту
      alarmRaise = -1;
      pixels.fill(mBlack);
      pixels.setBrightness(ledBrightness);
      ledActive = false;
    }
  }
}

void calcDawn() {
  for (int i = 0; i < al_kol; i++) {
    int minutesFromMid = alarms[i].hour * 60 + alarms[i].minute, dawnMinutes;
    if (minutesFromMid >= dawnTime) {
      dawnMinutes = minutesFromMid - dawnTime;
    } else {
      dawnMinutes = minutesFromMid + 1440 - dawnTime;
    }
    alarms[i].dawnHour = dawnMinutes / 60;
    alarms[i].dawnMin = dawnMinutes % 60;
  }
}

void dawnTick() {
  static uint32_t lightTime;
  if (!ledActive) {
    for (int i = 0; i < al_kol; i++) {
      if ((alarms[i].dawnHour == t_now.hour()) and (alarms[i].dawnMin == t_now.minute()) and (t_now.second() == 0) and alarms[i].isActive) {
        ledActive = true;
        lightTime = millis();
        dawnTemp = 900;
        brightTemp = 0;
#if (DEBUG == 1)
        Serial.print(i);
        Serial.println(F(" alarm: starting dawn "));
#endif
        break;
      }
    }
  }
  if (ledActive) {
    if (millis() - lightTime > dawnStep) {
      brightTemp++;
      if (brightTemp <= 255) {
        pixels.setBrightness(brightTemp);
      }
      pixels.fill(mKelvin(dawnTemp));
      pixels.show();
      dawnTemp++;
      lightTime = millis();
    }
  }
}
