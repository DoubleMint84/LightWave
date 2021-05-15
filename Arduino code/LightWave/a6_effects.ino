void randomMode() {
  static int nowMode = 0;
  static byte nowParam = 100;
  switch (nowMode) {
      case 0: randomMode(); break;
      case 1: rainbow(nowParam); break;
      case 2: colorCycle(); break;
      case 3: runningDots(nowParam); break;
      case 4: twinkleRandom(nowParam); break;
      case 5: strobe(); break;
      case 6: scanner(nowParam, 4); break;
  }
  if (randomTimer.isReady()) {
    nowMode = random(6);
    nowParam = random(255);
    randomTimer.setInterval(random(30000));
  }
}

void rainbow(byte param) {
  static byte counter = 0;
  if (ledActive) {
    return;
  }
  for (int i = 0; i < NUMPIXELS; i++) {
    pixels.set(i, mWheel8(counter + i * 255 / NUMPIXELS));   // counter смещает цвет
  }
  counter += map(param, 0, 255, 0, 15);   // counter имеет тип byte и при достижении 255 сбросится в 0
}

void colorCycle() {
  static byte counter = 0;
  pixels.fill(mWheel8(counter));
  counter += 3;
}

void runningDots(byte param) {
  static byte counter = 0;
  if (!dotsTimer.isReady()) return;
  for (int i = 0; i < NUMPIXELS - 1; i++) pixels.leds[i] = pixels.leds[i + 1];
  if (counter % 3 == 0) pixels.leds[NUMPIXELS - 1] = mWheel8(param);
  else pixels.leds[NUMPIXELS - 1] = mBlack;
  counter++;
}

void twinkleRandom(byte param) {
  static byte counter = 0;
  if (!dotsTimer.isReady()) return;
  byte paramTrans = map(param, 0, 255, 0, 100);
  if (counter > paramTrans) {
    counter = 0;
    pixels.fill(mBlack);
  } else {
    counter++;
    pixels.leds[random(NUMPIXELS)] = mWheel8(random(255));
  }
}

void strobe() {
  static bool stateStrobe = false;
  if (!dotsTimer.isReady()) return;
  if (stateStrobe) {
    pixels.fill(mBlack);
  } else {
    pixels.fill(mWhite);
  }
  stateStrobe = !stateStrobe;
}

void scanner(byte param, int eyeSize) {
  static bool directionScan = false;
  static byte color = 0;
  static int pos = 0;
  if (!directionScan) {
    pixels.fill(mBlack);
    if (pos < NUMPIXELS - eyeSize - 2) {
      pixels.set(pos, mWheel8(color, 100));
      pixels.set(pos + eyeSize + 1, mWheel8(color, 100));
      for (int j = 1; j <= eyeSize; j++) {
        pixels.leds[pos + j] = mWheel8(color);
      }
      pos++;
      color += map(param, 0, 255, 1, 20);
    } else {
      directionScan = true;
    }
  } else {
    pixels.fill(mBlack);
    if (pos > 0) {
      pixels.set(pos, mWheel8(color, 100));
      pixels.set(pos + eyeSize + 1, mWheel8(color, 100));
      for (int j = 1; j <= eyeSize; j++) {
        pixels.leds[pos + j] = mWheel8(color);
      }
      pos--;
      color += map(param, 0, 255, 1, 20);
    } else {
      directionScan = false;
    }
  }
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
