// Рассвет и будильники

void alarmTick() {
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
    if (millis() - timerAlarm >= 300000){ // если через 5 минут не была нажата кнопка, то выключаем ленту
      alarmRaise = -1;
      ledSetColor(0, 0, 0);
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
  if (!ledActive) {
    for (int i = 0; i < al_kol; i++) {
      if ((alarms[i].dawnHour == t_now.hour()) and (alarms[i].dawnMin == t_now.minute()) and (t_now.second() == 0) and alarms[i].isActive) {
        ledActive = true;
        lightTime = millis();
#if (DEBUG == 1)
        Serial.print(i);
        Serial.println(F(" alarm: starting dawn "));
#endif
        break;
      }
    }
  }
  if (ledActive) {
    if (ledR == 0 && ledG < 255) {
      if (millis() - lightTime > dawnStep) {
#if (DEBUG == 1)
        Serial.print(ledR);
        Serial.print(' ');
        Serial.println(ledG);
#endif
        //fillColor(pixels.Color(ledR, ledG, 0));
        pixels.fill(mRGB(ledR, ledG, 0)); // заливаем жёлтым
        pixels.show();
        ledG++;
        lightTime = millis();
      }
    }
    // плавно добавляем Красный
    if (ledR < 255 && ledG == 255) {
      if (millis() - lightTime > dawnStep) {
#if (DEBUG == 1)
        Serial.print(ledR);
        Serial.print(' ');
        Serial.println(ledG);
#endif
        pixels.fill(mRGB(ledR, ledG, 0)); // заливаем жёлтым
        pixels.show();
        ledR++;
        lightTime = millis();
      }
    }
    // плавно гасим Зеленый
    if (ledR == 255) {
      if (millis() - lightTime > dawnStep) {

        if (ledG != 0) {
#if (DEBUG == 1)
          Serial.print(ledR);
          Serial.print(' ');
          Serial.println(ledG);
#endif
          pixels.fill(mRGB(ledR, ledG, 0)); // заливаем жёлтым
          pixels.show();
          ledG--;
        }
        lightTime = millis();
      }
    }
  }
}
