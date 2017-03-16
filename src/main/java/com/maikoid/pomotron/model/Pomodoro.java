package com.maikoid.pomotron.model;

public class Pomodoro extends Chronometer implements IChronometer  {
	
	@Override
	public void pause() {
		stop();
	}
}
