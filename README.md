# LightWave
Multifunctional color garland that can be controlled from a smartphone.
## Информация по девайсу

### Протокол настроек
- $0; - cинхронизация (вывод всех параметров девайса)
- $1 0 \<hour\> \<min\> \<sec\>; - установка времени
- $1 1 \<day\> \<month\> \<year\>; - установка даты
- $1 2 \<i\> \<state\>; - смена состояния i-го будильника (НУМЕРАЦИЯ НАЧИНАЕТСЯ С 0)
- $1 3 \<i\> \<hour\> \<min\>; - смена времени i-го будильника (НУМЕРАЦИЯ НАЧИНАЕТСЯ С 0)
- $1 4 \<min\>; - установка времени рассвета (в минутах)
- $2 0; - выключение света ленты
- $2 1 \<red\> \<green\> \<blue\>; - включение света ленты в формате RGB
- $2 2 0 \<brightness\>; - регулировка яркости ленты (0-255)
- $2 2 1 \<state>; - включение/выключение режима дыхания
- $2 3 \<effect\>; - выбор эффекта
- $2 4 \<speed\>; - установка скорости эффекта
- $2 5 \<param\>; - установка параметра для эффекта

### Перечень эффектов
- 0 - смена режимов
- 1 - бегущая радуга
- 2 - циклические цвета
- 3 - бегущие точки
- 4 - случайные точки
- 5 - стробоскоп
- 6 - сканнер из будущего