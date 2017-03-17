package com.maikoid.pomotron.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import com.maikoid.pomotron.model.IChronometer;
import com.maikoid.pomotron.model.IPomodoroCreator;
import com.maikoid.pomotron.model.Subject;
import com.maikoid.pomotron.model.Observer;
import com.maikoid.pomotron.model.Pomodoro;
import com.maikoid.pomotron.model.PomodoroFactory;

public class SinglePomodoroExecuter implements Observer, Subject {
	public enum PomodoroType {
		INACTIVE("in"), WORK("Work"), SHORT_BREAK("Short Break"), LONG_BREAK("Long Break");
		
		private final String text;

		private PomodoroType(final String text) {
			this.text = text;
		}    

		@Override
		public String toString() {
			return text;
		}
	}

	public PomodoroType type;

	private Timer timer;
	private static int completedPomodoros = 0;
	private Pomodoro p = null;
	
	protected Set<Observer> obs;

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
			p.detach(this);			
		}
	}

	public Pomodoro getPomodoro() {
		return p;
	}

	public static int getCompletedPomodoros() {
		return completedPomodoros;
	}
	
	public SinglePomodoroExecuter(PomodoroType type, Observer o) {
		this(type);		
		attach(o);
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
		
		this.obs = Collections.synchronizedSet(new HashSet<Observer>());
	}

	public void update(Subject s) {
		if (s instanceof Pomodoro) {
			IChronometer p = (Pomodoro) s;
			switch (p.getState()) {
			case RUNNING:
				break;
			case PAUSED:				
			case STOPPED:
				break;
			case FINISHED:
				if (type == PomodoroType.WORK) {					
					completedPomodoros++;
					notifyObservers();					
				}
				break;
			}
		}
	}
	
	@Override
	public void attach(Observer o) {
		synchronized (obs) {
			if (o != null)
				obs.add(o);
		}

	}

	@Override
	public void detach(Observer o) {
		synchronized (obs) {
			if (o != null && obs.contains(o))
				obs.remove(o);
		}
	}

	@Override
	public void notifyObservers() {
		synchronized (obs) {
			for (Observer o : obs)
				o.update(this);
		}
	}

}
