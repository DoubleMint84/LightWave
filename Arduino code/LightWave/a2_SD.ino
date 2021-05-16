// Запись и считывание данных с SD-карты

void dataSdRead() {
  File myFile;
  myFile = SD.open(F("data.txt"));
  if (myFile) {
#if (DEBUG == 1)
    Serial.println(F("File has opened, reading data..."));
    Serial.println(F("Alarm # || Hour || Minute || State"));
#endif
    for (int i = 0; i < al_kol; i++) {
      alarms[i].hour = int8_t(myFile.parseInt());
      alarms[i].minute = int8_t(myFile.parseInt());
      alarms[i].isActive = bool(myFile.parseInt());
#if (DEBUG == 1)
      Serial.print(i + 1);
      Serial.print(F(" alarm: "));
      Serial.print(alarms[i].hour);
      Serial.print(F(" "));
      Serial.print(alarms[i].minute);
      if (alarms[i].isActive) {
        Serial.println(F(" ON"));
      } else {
        Serial.println(F(" OFF"));
      }
#endif
    }
    dawnTime = myFile.parseInt();
    myFile.close();
  } else {
#if (DEBUG == 1)
    Serial.println(F("ERROR: Could not read command file. Program stopped."));
#endif
    return;
  }
#if (DEBUG == 1)
  Serial.println(F("Data has written"));
#endif
}

void writeAlarmToSd(byte event) {
  switch (event) {
    case 0:
#if (DEBUG == 1)
      Serial.print(change_time);
      Serial.print(F(" alarm: set time to "));
      Serial.print(alarms[change_time - 1].hour);
      Serial.print(F(":"));
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


  SD.remove(F("data.txt"));
  File myFile = SD.open(F("data.txt"), FILE_WRITE);
  if (myFile) {
    for (int i = 0; i < al_kol; i++) {
      myFile.print(alarms[i].hour);
      myFile.print(F(" "));
      myFile.print(alarms[i].minute);
      myFile.print(F(" "));
      if (alarms[i].isActive) {
        myFile.println(F("1"));
      } else {
        myFile.println(F("0"));
      }
    }
    myFile.println(dawnTime);
    myFile.close();
  }
}
