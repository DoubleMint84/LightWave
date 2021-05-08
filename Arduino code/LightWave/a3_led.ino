// Функции для работы с лентой


void ledSetColor (byte r, byte g, byte b) {
  pixels.fill(mRGB(r, g, b)); // // заливка всей гирлянды одним цветом
  pixels.show();
}

void effectTick() {
  if (effectTimer.isReady() and !ledActive) {
    switch (ledEffect) {
      case -1:
        pixels.clear();
        break;
      case 0:
        rainbow();
        break;
    }
    if (is_breath) {
      breathing();
    } else {
      pixels.setBrightness(ledBrightness);
    }
    pixels.show();
  }
}
/*
  void fillColor(uint32_t c) {
  for (uint16_t i = 0; i < pixels.numPixels(); i++) {
    // заполняем текущий сегмент выбранным цветом
    pixels.setPixelColor(i, c);
    pixels.show();
  }
  }*/
