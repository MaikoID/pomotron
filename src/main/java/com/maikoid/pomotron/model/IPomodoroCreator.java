package com.maikoid.pomotron.model;

public interface IPomodoroCreator{

	IChronometer workPomodoro();
	IChronometer longBreakPomodoro();
	IChronometer shortBreakPomodoro();
}
