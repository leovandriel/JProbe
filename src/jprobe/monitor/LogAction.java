package jprobe.monitor;

import jprobe.tracker.Tracker;

public class LogAction implements ThreadAction {
	public void run(Thread thread) {
		System.out.println("Thread gone haywire!");
		Tracker.printStackTrace(thread, System.out);
	}
}
