package com.maikoid.pomotron.model;

import java.util.*;
import java.util.TimerTask;

import org.joda.time.Duration;

public class Chronometer extends TimerTask implements IChronometer, Cloneable {

	protected Duration time;
	protected Set<Observer> obs;
	private STATE state = null;

	public Chronometer() {
		this.obs = Collections.synchronizedSet(new HashSet<Observer>());
	}

	@Override
	public void start() {
		if (getState() != STATE.RUNNING) {
			setState(STATE.RUNNING);
		}
	}

	@Override
	public void pause() {
		setState(STATE.PAUSED);
	}

	@Override
	public void stop() {
		setState(STATE.STOPPED);
	}

	@Override
	public void setTime(Duration newTime) {
		time = newTime;
	}

	@Override
	public Duration getTime() {
		return time;
	}

	@Override
	public STATE getState() {
		return this.state;
	}

	protected void setState(STATE state) {
		if (getState() != state) {
			this.state = state;
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

	/**
	 * Task that is triggered every second.
	 */
	@Override
	public void run() {
		// check if the state is ready (start methods was called).
		// Avoids error in case when the timer is triggered before the
		// start method.
		if (getState() != null) {
			switch (getState()) {
			case RUNNING:
				if (time.getStandardSeconds() > 0) {
					time = time.minus(1 * 1000);
					notifyObservers();
				} else
					setState(STATE.FINISHED);
				break;
			case PAUSED:
			case STOPPED:
			case FINISHED:
				this.cancel();
				notifyObservers();
				break;
			}
		}
	}

}
