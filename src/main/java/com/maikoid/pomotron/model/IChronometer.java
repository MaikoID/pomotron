package com.maikoid.pomotron.model;

import org.joda.time.Duration;

public interface IChronometer extends Subject{

	enum STATE { 
		STOPPED("stopped"),
		RUNNING("running"),
		PAUSED("paused"),
		FINISHED("finished");

		private final String text;

		private STATE(final String text) {
			this.text = text;
		}    

		@Override
		public String toString() {
			return text;
		}
	}

	void start();
	void pause();
	void stop();
	void setTime(Duration time);
	Duration getTime();	
	STATE getState();
}
