// Запись и считывание данных с SD-карты

void dataSdRead() {
  File myFile;
  myFile = SD.open("data.txt");
  if (myFile) {
#if (DEBUG == 1)
    Serial.println("File has opened, reading data...");
    Serial.println("Alarm # || Hour || Minute || State");
#endif
    for (int i = 0; i < al_kol; i++) {
      alarms[i].hour = int8_t(myFile.parseInt());
      alarms[i].minute = int8_t(myFile.parseInt());
      alarms[i].isActive = bool(myFile.parseInt());
#if (DEBUG == 1)
      Serial.print(i + 1);
      Serial.print(" alarm: ");
      Serial.print(alarms[i].hour);
      Serial.print(' ');
      Serial.print(alarms[i].minute);
      if (alarms[i].isActive) {
        Serial.println(" ON");
      } else {
        Serial.println(" OFF");
      }
#endif
    }
    myFile.close();
  } else {
#if (DEBUG == 1)
    Serial.println("ERROR: Could not read command file. Program stopped.");
#endif
    return;
  }
#if (DEBUG == 1)
  Serial.println("Data has written");
#endif
}

void writeAlarmToSd(byte event) {
  switch (event) {
    case 0:
#if (DEBUG == 1)
      Serial.print(change_time);
      Serial.print(F(" alarm: set time to "));
      Serial.print(alarms[change_time - 1].hour);
      Serial.print(':');
      Serial.print(alarms[change_time - 1].minute);
      Serial.print(F(". Writing to file..."));
#endif
      break;
    case 1:
#if (DEBUG == 1)
      Serial.print(F("alarm: changing state..."));
#endif
      break;
  }


  SD.remove("data.txt");
  File myFile = SD.open("data.txt", FILE_WRITE);
  if (myFile) {
    for (int i = 0; i < al_kol; i++) {
      myFile.print(alarms[i].hour);
      myFile.print(' ');
      myFile.print(alarms[i].minute);
      myFile.print(' ');
      if (alarms[i].isActive) {
        myFile.println("1");
      } else {
        myFile.println("0");
      }
    }
    myFile.close();
  }
}
