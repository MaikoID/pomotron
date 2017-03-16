package com.maikoid.pomotron.model;

public class PomodoroFactory implements IPomodoroCreator{

	public static int WorkingTime = 25;

	public int LongBreakTime = 15;

	public int ShortBreakTime = 5;

	public IChronometer workPomodoro(){ 
		Pomodoro p = new Pomodoro();
		p.setMinutes(WorkingTime);
		return p;
	}

	public IChronometer longBreakPomodoro(){ 
		Pomodoro p = new Pomodoro();
		p.setMinutes(LongBreakTime);
		return p;
	}

	public IChronometer shortBreakPomodoro(){ 
		Pomodoro p = new Pomodoro();
		p.setMinutes(ShortBreakTime);
		return p;
	}

	public IChronometer createChronometer() {
		return null;
	}
}
