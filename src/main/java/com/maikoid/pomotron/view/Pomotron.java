package com.maikoid.pomotron.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.maikoid.pomotron.controller.SinglePomodoroExecuter;
import com.maikoid.pomotron.controller.SinglePomodoroExecuter.PomodoroType;
import com.maikoid.pomotron.model.IChronometer;
import com.maikoid.pomotron.model.Observer;
import com.maikoid.pomotron.model.Pomodoro;
import com.maikoid.pomotron.model.Subject;
import com.maikoid.pomotron.model.IChronometer.STATE;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;

public class Pomotron extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SystemTray tray;

	Menu pomodoroCountItem;
	MenuItem workMenuItem;
	MenuItem sBreakMenuItem;
	MenuItem lBreakMenuItem;
	MenuItem cancelItem;
	MenuItem exitItem;

	BufferedImage imgNotify;
	BufferedImage imgSystrayIcon;

	SinglePomodoroExecuter pomExec;

	IChronometer.STATE lastPomodoroState;

	@Override
	public void update(Subject s) {
		if (s instanceof Pomodoro) {
			Pomodoro p = (Pomodoro) s;

			// Notifies are only sent for new states
			if (lastPomodoroState == null || lastPomodoroState != p.getState()) {

				lastPomodoroState = p.getState();

				switch (p.getState()) {
				case RUNNING:
					switch (pomExec.type) {
					case WORK:
						PomotronGUIHelper.displayNotification(imgNotify, "Pomodoro", "Time to WORK!");
						break;
					case SHORT_BREAK:
					case LONG_BREAK:
						PomotronGUIHelper.displayNotification(imgNotify, "Pomodoro Break", "Time to RELAX!");
						break;
					case INACTIVE:
						break;
					}
					break;
				case PAUSED:
				case STOPPED:
					switch (pomExec.type) {
					case WORK:
						PomotronGUIHelper.displayNotification(imgNotify, "Pomodoro Interrupted",
								"Start a new one when You are feeling ready!");
						tray.setStatus("Pomodoro Interrupted");
						break;
					case SHORT_BREAK:
					case LONG_BREAK:
						PomotronGUIHelper.displayNotification(imgNotify, "Pomodoro Break Interrupted",
								"Start a new Pomodoro if You are feeling ready!");
						tray.setStatus("Pomodoro Break Interrupted");
						break;
					case INACTIVE:
						break;
					}
					PomodoroEndedExecution();
					break;
				case FINISHED:
					switch (pomExec.type) {
					case WORK:
						PomotronGUIHelper.displayNotification(imgNotify, "Pomodoro Finished", "Time for a break!");
						tray.setStatus("Pomodoro Finished");
						break;
					case SHORT_BREAK:
					case LONG_BREAK:
						PomotronGUIHelper.displayNotification(imgNotify, "Pomodoro Break Finished",
								"You can start a new pomodoro when feeling ready =)");
						tray.setStatus(pomExec.type + " Finished");
						break;
					case INACTIVE:
						break;
					}
					PomodoroEndedExecution();
					break;
				}
			}
			displayExeInfo(pomExec);
		}
		if (s instanceof SinglePomodoroExecuter) {
			SinglePomodoroExecuter pe = (SinglePomodoroExecuter) s;

			switch (pe.type) {
			case WORK:
				MenuItem currentTime = new MenuItem(new DateTime().toString(DateTimeFormat.forPattern("HH:mm:ss")));
				currentTime.setEnabled(false);
				pomodoroCountItem.setText(
						String.format("Finished Pomodoros: %d", SinglePomodoroExecuter.getCompletedPomodoros()));
				pomodoroCountItem.add(currentTime);
				break;
			case SHORT_BREAK:
			case LONG_BREAK:
			case INACTIVE:
				break;
			}

			displayExeInfo(pe);

		}
	}

	public Pomotron() {

		try {
			imgNotify = ImageIO.read(Pomotron.class.getClassLoader().getResource("icons/48x48/pomotron.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			imgSystrayIcon = ImageIO.read(Pomotron.class.getClassLoader().getResource("icons/24x24/pomotron.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tray = null;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				populateMenu();
			}
		});

		setVisible(false);
	}

	/**
	 * Updates the tray icon and tooltip
	 * 
	 * @param pomodoro
	 */
	private void displayExeInfo(SinglePomodoroExecuter pe) {
		long secsLeft = 0;
		String timeLeftString = "";
		String timeLeft = "";

		// just take the time from Pomodoro that is running
		if (pe.getPomodoro().getState() == STATE.RUNNING) {
			secsLeft = pe.getPomodoro().getTime().getStandardSeconds();
			if (secsLeft > 59) {
				timeLeft = String.valueOf(pe.getPomodoro().getTime().getStandardMinutes());
			} else {
				timeLeft = String.valueOf(secsLeft);
			}

			PeriodFormatter pf = new PeriodFormatterBuilder().printZeroAlways().minimumPrintedDigits(2).appendMinutes()
					.appendSeparator(":").appendSeconds().toFormatter();

			timeLeftString = pf.print(pe.getPomodoro().getTime().toPeriod());
		}	

		switch (pe.type) {
		case WORK:
			if (secsLeft > 0) {
				tray.setImage(PomotronGUIHelper.createIcon(timeLeft, imgSystrayIcon, 0.75));
				tray.setStatus(String.format("Pomodoro: %s.", timeLeftString).toString());
			}
			break;
		case SHORT_BREAK:
			if (secsLeft > 0) {
				tray.setImage(PomotronGUIHelper.createIcon(timeLeft, imgSystrayIcon, 0.75));
				tray.setStatus(String.format("Short Break: %s.", timeLeftString).toString());
			}
			break;
		case LONG_BREAK:
			if (secsLeft > 0) {
				tray.setImage(PomotronGUIHelper.createIcon(timeLeft, imgSystrayIcon, 0.75));
				tray.setStatus(String.format("Long Break: %s.", timeLeftString).toString());
			}
			break;
		case INACTIVE:
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
		tray.setImage(PomotronGUIHelper.createIcon("", imgSystrayIcon, 0.75));
	}

	private void PomodoroEndedExecution() {
		resetGUIPrePomodoro();
		lastPomodoroState = null;
	}

	/**
	 * Execute state transition and update UI/timers accordingly
	 * 
	 * @param type
	 *            new type to assume
	 */
	private void execByPomodoroType(PomodoroType type) {
		// to enable Restart without cancel it first
		// reset the GUI in either case
		PomodoroEndedExecution();
		if (pomExec != null) {
			// end the current execution
			pomExec.end();
			// if there is a pomExec there is a Pomodoro too
			// so detach from the Subjects now
			pomExec.getPomodoro().detach(this);
			pomExec.detach(this);
			
		}

		switch (type) {
		case WORK:
			workMenuItem.setText("Restart Pomodoro");
			cancelItem.setText("Cancel Pomodoro");
			pomExec = new SinglePomodoroExecuter(PomodoroType.WORK, this);
			break;
		case SHORT_BREAK:
			sBreakMenuItem.setText("Restart Short Break");
			cancelItem.setText("Cancel Break");
			pomExec = new SinglePomodoroExecuter(PomodoroType.SHORT_BREAK, this);
			break;
		case LONG_BREAK:
			lBreakMenuItem.setText("Restart Long Break");
			cancelItem.setText("Cancel Break");
			pomExec = new SinglePomodoroExecuter(PomodoroType.LONG_BREAK, this);
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
		tray = SystemTray.get();
		if (tray == null) {
			throw new RuntimeException("Unable to load SystemTray!");
		}
		tray.setImage(PomotronGUIHelper.createIcon("", imgSystrayIcon, 0.75));

		pomodoroCountItem = new Menu("Completed Pomodoros: 0");

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
