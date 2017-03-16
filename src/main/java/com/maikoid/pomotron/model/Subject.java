package com.maikoid.pomotron.model;

public interface Subject {
	void attach(Observer o);
	void dettach(Observer o);
	void notifyObservers();
}
