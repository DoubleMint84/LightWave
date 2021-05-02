// Функции для работы с лентой

void rgbSetPreset () {
  if (!ledActive) {

    pixels.fill(mRGB(ledPreset.r, ledPreset.g, ledPreset.b)); // заливка всей гирлянды одним цветом
    pixels.show();
  }
}

void ledSetColor (byte r, byte g, byte b) {
  pixels.fill(mRGB(r, g, b)); // // заливка всей гирлянды одним цветом
  pixels.show();
}
/*
  void fillColor(uint32_t c) {
  for (uint16_t i = 0; i < pixels.numPixels(); i++) {
    // заполняем текущий сегмент выбранным цветом
    pixels.setPixelColor(i, c);
    pixels.show();
  }
  }*/
