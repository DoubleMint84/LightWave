// ПАРСИНГ ВХОДЯЩИХ ДАННЫХ И ИХ ОБРАБОТКА

void command_parse() {
  if (recievedFlag) {                           // если получены данные
    recievedFlag = false;
#if (DEBUG == 1)
    for (int i = 0; i < index; i++) {
      Serial.print(intData[i]); Serial.print(" ");

    } 
    Serial.println();
#endif
    switch (intData[0]) {
      case 0:
        for (int i = 0; i < 5; i++) {
          blueTooth.print("1 3 ");
          blueTooth.print(i);
          blueTooth.print(' ');
          blueTooth.print(alarms[i].hour);
          blueTooth.print(' ');
          blueTooth.print(alarms[i].minute);
          blueTooth.print(' ');
          blueTooth.println(alarms[i].isActive ? "1" : "0");
        }
        break;

      case 1:
        switch (intData[1]) {
          case 0:
            t_now = rtc.now();
            rtc.adjust(DateTime(t_now.year(), t_now.month(), t_now.day(), intData[2], intData[3], intData[4]));
            t_now = rtc.now();
            disp.displayClock(byte(t_now.hour()), byte(t_now.minute()));
            t_prev = t_now;
            break;
          case 1:
            t_now = rtc.now();
            rtc.adjust(DateTime(intData[4], intData[3], intData[2], t_now.hour(), t_now.minute(), t_now.second()));
            break;
          case 2:
            alarms[intData[2]].isActive = (intData[3] == 1);
            writeAlarmToSd(1);
            break;
          case 3:
            alarms[intData[2]].hour = intData[3];
            alarms[intData[2]].minute = intData[4];
            writeAlarmToSd(0);
            calcDawn();
            break;
        }
        break;
      case 2:
        switch (intData[1]) {
          case 0:
            pixels.clear();   // очищает буфер
            // применяется при выводе .show() !
            pixels.show();
            break;
          case 1:
            pixels.fill(mRGB(intData[3], intData[2], intData[4])); // заливаем жёлтым
            pixels.show();
            break;
          case 2:
            switch (intData[2]) {
              case 0:
                ledBrightness = intData[3];
                pixels.setBrightness(intData[3]);
                pixels.show();
                break;
              case 1:
                is_breath = intData[3];
                break;
            }
            break;
          case 3:
            ledEffect = intData[2];
            break;
        }
        break;
    }
  }
}

void parsing() {
  if (blueTooth.available() > 0) {

    char incomingByte = blueTooth.read();        // обязательно ЧИТАЕМ входящий символ
    if (getStarted) {                         // если приняли начальный символ (парсинг разрешён)
      if (incomingByte != ' ' && incomingByte != ';') {   // если это не пробел И не конец
        string_convert += incomingByte;       // складываем в строку
      } else {                                // если это пробел или ; конец пакета
        intData[index] = string_convert.toInt();  // преобразуем строку в int и кладём в массив
        string_convert = "";                  // очищаем строку
        index++;                              // переходим к парсингу следующего элемента массива
      }
    }
    if (incomingByte == '$') {                // если это $
#if (DEBUG == 1)
      Serial.println("Data detected");
#endif
      getStarted = true;                      // поднимаем флаг, что можно парсить
      index = 0;                              // сбрасываем индекс
      string_convert = "";                    // очищаем строку
    }
    if (incomingByte == ';') {                // если таки приняли ; - конец парсинга
      getStarted = false;                     // сброс
      recievedFlag = true;                    // флаг на принятие
    }
  }
}
