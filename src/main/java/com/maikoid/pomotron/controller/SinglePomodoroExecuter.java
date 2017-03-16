package com.maikoid.pomotron.controller;

import java.util.Timer;

import com.maikoid.pomotron.model.IChronometer;
import com.maikoid.pomotron.model.IPomodoroCreator;
import com.maikoid.pomotron.model.Subject;
import com.maikoid.pomotron.model.Observer;
import com.maikoid.pomotron.model.Pomodoro;
import com.maikoid.pomotron.model.PomodoroFactory;

public class SinglePomodoroExecuter implements Observer {
	public enum PomodoroType {
		INACTIVE, WORK, SHORT_BREAK, LONG_BREAK
	}

	public PomodoroType type;

	private Timer timer;
	private static int completedPomodoros = 0;
	private Pomodoro p = null;

	public void start() throws IllegalStateException {
		if (timer != null)
			throw new IllegalStateException("Pomorodo already started!");

		p.start();
		timer = new Timer();
		timer.scheduleAtFixedRate(p, 0, 1000L);
	}

	public void end() {
		if (p != null) {
			p.stop();
			type = PomodoroType.INACTIVE;			
			
			timer.purge();
			timer.cancel();
		}
	}

	public Pomodoro getPomodoro() {
		return p;
	}

	public static int getCompletedPomodoros() {
		return completedPomodoros;
	}

	public SinglePomodoroExecuter(PomodoroType type) {
		IPomodoroCreator icc = new PomodoroFactory();
		this.type = type;

		switch (this.type) {
		case WORK:
			p = (Pomodoro) icc.workPomodoro();
			break;
		case SHORT_BREAK:
			p = (Pomodoro) icc.shortBreakPomodoro();
			break;
		case LONG_BREAK:
			p = (Pomodoro) icc.longBreakPomodoro();
			break;
		default:
			throw new AssertionError("Invalid Pomodoro Type!");
		}
		p.attach(this);
	}

	public void update(Subject s) {
		if (s instanceof Pomodoro) {
			IChronometer p = (Pomodoro) s;
			switch (p.getState()) {
			case RUNNING:
				break;
			case PAUSED:
				break;
			case STOPPED:
				break;
			case FINISHED:
				if (type == PomodoroType.WORK)
					completedPomodoros++;

				break;
			}
		}
	}

}
