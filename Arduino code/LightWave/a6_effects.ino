void rainbow() {
  static byte counter = 0;
  if (ledActive) {
    return;
  }
  for (int i = 0; i < NUMPIXELS; i++) {
    pixels.set(i, mWheel8(counter + i * 255 / NUMPIXELS));   // counter смещает цвет
  }
  counter += 3;   // counter имеет тип byte и при достижении 255 сбросится в 0
}

void breathing() {
  static int dir = 1;
  static int bright = 0;
  if (ledActive) {
    return;
  }
  bright += dir * 5;    // 5 - множитель скорости изменения

  if (bright > 255) {
    bright = 255;
    dir = -1;
  }
  if (bright < 0) {
    bright = 0;
    dir = 1;
  }
  pixels.setBrightness(bright);
}
