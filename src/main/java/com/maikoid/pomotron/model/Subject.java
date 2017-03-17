package com.maikoid.pomotron.model;

public interface Subject {
	void attach(Observer o);
	void detach(Observer o);
	void notifyObservers();
}
