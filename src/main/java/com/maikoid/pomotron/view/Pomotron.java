package com.maikoid.pomotron.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;

import com.maikoid.pomotron.controller.SinglePomodoroExecuter;
import com.maikoid.pomotron.controller.SinglePomodoroExecuter.PomodoroType;
import com.maikoid.pomotron.model.*;

public class Pomotron implements Observer {

	private final SystemTray tray;

	MenuItem pomodoroCountItem;
	MenuItem workMenuItem;
	MenuItem sBreakMenuItem;
	MenuItem lBreakMenuItem;
	MenuItem cancelItem;
	MenuItem exitItem;

	SinglePomodoroExecuter pomExec;

	public void update(Subject s) {
		if (s instanceof Pomodoro) {
			IChronometer p = (Pomodoro) s;

			switch (p.getState()) {
			case RUNNING:
				break;
			case PAUSED:
			case STOPPED:
			case FINISHED:
				cancelPomodoroExecution();
				break;
			}
			updatePomodoroInfo(p);
		}
	}

	public Pomotron() {
		tray = SystemTray.get();
		if (tray == null) {
			throw new RuntimeException("Unable to load SystemTray!");
		}

		tray.setImage(PomotronUtils.createTrayIconImage("P", 0.75));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				populateMenu();
			}
		});
	}

	/**
	 * Updates the tray icon and tooltip
	 * 
	 * @param pomodoro
	 */
	private void updatePomodoroInfo(IChronometer pomodoro) {
		int secsLeft = 0;
		String timeLeftString = "";
		String timeLeft = "";

		// only retrieve the pomodoro current time if its a valid pomodoro
		if (pomExec.type != SinglePomodoroExecuter.PomodoroType.INACTIVE) {
			secsLeft = pomodoro.getCurrentTime();
			if (secsLeft > 60) {
				timeLeft = Integer.toString(secsLeft / 60);
			} else {
				timeLeft = Integer.toString(secsLeft);
			}
			timeLeftString = String.format("%02d:%02d", (secsLeft / 60), secsLeft % 60);
		}

		pomodoroCountItem
				.setText(String.format("Completed Pomodoros: %d", SinglePomodoroExecuter.getCompletedPomodoros()));

		switch (pomExec.type) {
		case WORK:
			tray.setImage(PomotronUtils.createTrayIconImage(timeLeft, 0.75));
			tray.setStatus(String.format("Pomodoro: %s.", timeLeftString).toString());
			break;
		case SHORT_BREAK:
			tray.setImage(PomotronUtils.createTrayIconImage(timeLeft, 0.75));
			tray.setStatus(String.format("Short Break: %s.", timeLeftString).toString());
			break;
		case LONG_BREAK:
			tray.setImage(PomotronUtils.createTrayIconImage(timeLeft, 0.75));
			tray.setStatus(String.format("Long Break: %s.", timeLeftString).toString());
			break;
		case INACTIVE:
			tray.setImage(PomotronUtils.createTrayIconImage("P", 0.75));
			break;
		}
	}

	private void resetGUIPrePomodoro() {
		// initial labels state
		workMenuItem.setText("New Pomodoro");
		sBreakMenuItem.setText("New Short Break");
		lBreakMenuItem.setText("New Long Break");
		cancelItem.setText("Cancel");
		cancelItem.setEnabled(false);
	}

	private void cancelPomodoroExecution() {
		resetGUIPrePomodoro();

		if (pomExec != null) {
			pomExec.type = PomodoroType.INACTIVE;
		}
	}

	/**
	 * Execute state transition and update UI/timers accordingly
	 * 
	 * @param type
	 *            new type to assume
	 */
	private void execByPomodoroType(PomodoroType type) {
		cancelPomodoroExecution();

		if (pomExec != null) {
			pomExec.getPomodoro().dettach(this);
		}

		switch (type) {
		case WORK:
			workMenuItem.setText("Restart Pomodoro");
			cancelItem.setText("Cancel Pomodoro");
			pomExec = new SinglePomodoroExecuter(PomodoroType.WORK);
			break;
		case SHORT_BREAK:
			sBreakMenuItem.setText("Restart Short Break");
			cancelItem.setText("Cancel Break");
			pomExec = new SinglePomodoroExecuter(PomodoroType.SHORT_BREAK);
			break;
		case LONG_BREAK:
			lBreakMenuItem.setText("Restart Long Break");
			cancelItem.setText("Cancel Break");
			pomExec = new SinglePomodoroExecuter(PomodoroType.LONG_BREAK);
			break;
		case INACTIVE:
			break;

		}

		if (type != PomodoroType.INACTIVE) {
			pomExec.getPomodoro().attach(this);
			cancelItem.setEnabled(true);
			pomExec.start();
		}
	}

	/**
	 * Add all items and listeners to the right click menu
	 */
	private void populateMenu() {
		pomodoroCountItem = new MenuItem("Completed Pomodoros: 0");

		workMenuItem = new MenuItem("Start Pomodoro", new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				execByPomodoroType(SinglePomodoroExecuter.PomodoroType.WORK);
			}
		});

		sBreakMenuItem = new MenuItem("Short Break", new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				execByPomodoroType(SinglePomodoroExecuter.PomodoroType.SHORT_BREAK);
			}
		});

		lBreakMenuItem = new MenuItem("Long Break", new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				execByPomodoroType(SinglePomodoroExecuter.PomodoroType.LONG_BREAK);
			}
		});

		cancelItem = new MenuItem("Cancel", new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				assert pomExec != null : "GUI error! Canceling Pomodoro not executing.";
				pomExec.end();
			}
		});
		cancelItem.setEnabled(false);

		exitItem = new MenuItem("Exit", new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				tray.shutdown();
				System.exit(0);
			}
		});

		tray.getMenu().add(pomodoroCountItem);
		tray.getMenu().add(new Separator());
		tray.getMenu().add(workMenuItem);
		tray.getMenu().add(sBreakMenuItem);
		tray.getMenu().add(lBreakMenuItem);
		tray.getMenu().add(new Separator());
		tray.getMenu().add(cancelItem);
		tray.getMenu().add(new Separator());
		tray.getMenu().add(exitItem);
	}

	public static void main(String[] args) {
		new Pomotron();
	}
}
