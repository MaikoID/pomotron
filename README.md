# Pomotron
Simple and extensible Pomodoro timer in the system tray.

## Description 
Pomotron aims to be a simple Pomodoro timer with the following features:
- Extensible in every way;
- Interaction directly from the system tray;
- Cross-platform powered by Java + SystemTray;
- Completed Pomodoro count display;
- Deb package.

This application is inspired in Tomotron

## Usage
A shortcut (.desktop) file called Pomodoro is installed.
Click in the tray icon to start Pomodoros and Breaks.

Remaining time is given in minutes if more than a minute is left. If less then a minute is left, the remaining seconds are displayed.

## Ubuntu* INSTALL/UNINSTALL

Download the .deb package in ``target/pomotron_1.0~SNAPSHOT_all.deb``
```bash
	sudo dpkg -i pomotron_1.0~SNAPSHOT_all.deb
	sudo update-desktop-database
```

```bash
	sudo apt remove pomotron
```

* Ubuntu 16.10 tested.


## TODO

- Update the dorkbox Notify when the empty windows bug has been fixed.
- Create its own ppa for Ubuntu and MSI installer for windows.
