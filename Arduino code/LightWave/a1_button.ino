void buttonTick() {
  but.tick();
  if (but.isClick()) {
    if (ledActive) {
      alarmRaise = -1;
      pixels.fill(mBlack);
      ledActive = false;
    } else {
      ledEffect = -2;
    }
  } else if (but.isHolded()) {
    ledEffect = -1;
    pixels.setBrightness(ledBrightness);
  }
}
