void randomMode() {
  static int nowMode = 0;
  static byte nowParam = 100;
  switch (nowMode) {
    case 0:rainbow(nowParam); break;
    case 1: rainbow(nowParam); break;
    case 2: colorCycle(); break;
    case 3: runningDots(nowParam); break;
    case 4: twinkleRandom(nowParam); break;
    //case 5: strobe(); break;
    case 5: scanner(nowParam, 4); break;
    case 6: runningLights(nowParam); break;
    case 7: theatreChase(nowParam); break;
    case 8: xmasLights(nowParam); break;
    case 9: confetti_pal(nowParam); break;
    case 10: xmasLightsFade(nowParam); break;
  }
  if (randomTimer.isReady()) {
    nowMode = random(10);
    nowParam = random(255);
    randomTimer.setInterval(random(20000));
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

void runningLights(byte param) {
  static byte counter = 0;
  if (counter < NUMPIXELS * 2) {
    counter += 1;
    for (int i = 0; i < NUMPIXELS; i++) {

      pixels.set(i, mWheel8(param, (sin(i + counter) * 127 + 128)));
    }
  } else {
    counter = 0;
  }
}

byte abs0(double p) {
  if (p >= 0) {
    return byte(p);
  } else {
    return (byte)0;
  }
}

void theatreChase(byte param) {
  static byte counter = 0;
  if (!dotsTimer.isReady()) return;
  counter++;
  if (counter > 3) {
    counter = 0;
  }
  pixels.fill(mBlack);
  for (int i = 0; i < NUMPIXELS; i = i + 3) {
    pixels.set(i + counter, mWheel8(param));  //turn every third pixel on
  }
}

void xmasLights(byte param) {
  static byte counter = 0;
  static byte minimal = 50;
  static double mult = 0.005 * param;
  if (counter < ((2 * PI) / mult)) {
    counter += 1;
    for (int i = 0; i < NUMPIXELS; i += 4) {
      byte r = fade8(255, abs0(sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(r, 0, 0));
    }
    
    for (int i = 1; i < NUMPIXELS; i += 4) {
      byte r = fade8(255, abs0(-sin(mult * counter) * (255 - minimal)) + minimal);
      byte g = fade8(208, abs0(-sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(r, g, 0));
    }
    for (int i = 2; i < NUMPIXELS; i += 4) {
      byte g = fade8(255, abs0(sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(0, g, 0));
    }
    for (int i = 3; i < NUMPIXELS; i += 4) {
      byte b = fade8(255, abs0(-sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(0, 0, b));
    }
  } else {
    counter = 0;
  }
}

void confetti_pal(byte thisfade) { 
  static byte counter = 0;                                                                                            
  if (NUMPIXELS >= 10) {
    for (int i = 0; i < NUMPIXELS; i += 1) {
      
      pixels.set(i, mWheel8(counter, thisfade));
    }
    uint8_t pos = (uint8_t)random(0, NUMPIXELS);
    pixels.set(pos, mWheel8((uint8_t)random(0, 255)));
    counter += 1;                                                                                        
  }
}

void xmasLightsFade(byte param) {
  static byte counter = 0;
  static byte minimal = 50;
  static double mult = 0.005 * param;
  if (counter < ((2 * PI) / mult)) {
    counter += 1;
    for (int i = 0; i < NUMPIXELS; i += 4) {
      byte r = fade8(255, abs0(sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(r, 0, 0));
    }
    
    for (int i = 1; i < NUMPIXELS; i += 4) {
      byte r = fade8(255, abs0(sin(mult * counter) * (255 - minimal)) + minimal);
      byte g = fade8(208, abs0(sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(r, g, 0));
    }
    for (int i = 2; i < NUMPIXELS; i += 4) {
      byte g = fade8(255, abs0(sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(0, g, 0));
    }
    for (int i = 3; i < NUMPIXELS; i += 4) {
      byte b = fade8(255, abs0(sin(mult * counter) * (255 - minimal)) + minimal);
      pixels.set(i, mRGB(0, 0, b));
    }
  } else {
    counter = 0;
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
