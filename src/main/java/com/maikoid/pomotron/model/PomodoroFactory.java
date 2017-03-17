package com.maikoid.pomotron.model;

import org.joda.time.Duration;

public class PomodoroFactory implements IPomodoroCreator{

	public static Duration WorkingTime = new Duration(5*1000);

	public static Duration LongBreakTime = new Duration(5*1000);

	public static Duration ShortBreakTime = new Duration(5*1000);
	
	public IChronometer workPomodoro(){ 
		Pomodoro p = new Pomodoro();
		p.setTime(WorkingTime);		
		return p;
	}

	public IChronometer longBreakPomodoro(){ 
		Pomodoro p = new Pomodoro();
		p.setTime(LongBreakTime);
		return p;
	}

	public IChronometer shortBreakPomodoro(){ 
		Pomodoro p = new Pomodoro();
		p.setTime(ShortBreakTime);
		return p;
	}

	public IChronometer createChronometer() {
		return null;
	}
}
