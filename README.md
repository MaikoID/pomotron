# Pomotron
Simple and extensible pomodoro timer in the system tray.

## Description 
Pomotron aims to be a simple pomodoro timer with the following features:
- Extensible in every way
- Timer functions operatable from the system tray
- Cross-platform (at least Linux and Windows)
- Completed pomodoro count display
- Desktop notifications on completed pomodoro/break
- Minimal build dependencies

The applications started from a fork of Pomotron

## Usage
Run the jar file. A tray icon (``P`` on a grey background) is displayed.
Right click the tray icon to start pomodoros and breaks.

When a pomodoro or break is completed, a desktop notification is displayed.

The icon color and text is as follows
- Grey: Inactive and showing ``P`` displayed (stands for Pomodoro)
- Red: Pomodoro in progress, remaining time displayed
- Green: Short break in progress, remaining time displayed
- Blue: Long break in progress, remaining time displayed

Remaining time is given in minutes if more than a minute is left. If less then a minute is left, the remaining seconds are displayed.

## INSTALL
###Ubuntu with Unity
From ```pomotron``` directory execute:
```bash
# install.sh
```
Run from Unity Dash

###Another Enviroment
- From ```pomotron/src``` directory execute:
```bash
 # javac -verbose -d ../bin/classes ./pomotron/model/*.java ./pomotron/controller/*.java  ./pomotron/view/*.java && jar vcmf Manifest.txt ../bin/pomotron -C ../bin/classes .;
```
Run from terminal
```bash
 $ java -jar ../bin/pomotron
```
## UNINSTALL
###Ubuntu with Unity
- From ```pomotron``` directory execute:
```bash
# uninstall.sh
```
 


