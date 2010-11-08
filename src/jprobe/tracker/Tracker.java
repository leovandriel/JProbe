package jprobe.tracker;

import java.io.PrintStream;
import java.util.Map;

/**
 * A static facility that runs a daemon timer for repeated updating of a trace
 * log of all threads.
 */
public class Tracker {
	private static TraceLog log;
	private static final long interval = 1024;
	private static final String name = "Trace Tracker";
	static {
		Thread thread = new Thread(name) {
			public void run() {
				for (;;) {
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						break;
					}
					getMonitorSingleton().update(Thread.getAllStackTraces(), System.currentTimeMillis());
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Calls {@link TraceLog#getAllStackTraces()} on a static log.
	 */
	public static Map<Thread, TimedStackTraceElement[]> getAllStackTraces() {
		return getMonitorSingleton().getAllStackTraces();
	}

	/**
	 * Calls {@link TraceLog#getStackTrace(Thread thread)} on a static log.
	 */
	public static TimedStackTraceElement[] getStackTrace(Thread thread) {
		return getMonitorSingleton().getStackTrace(thread);
	}

	/**
	 * Calls {@link TraceLog#printStackTrace(Thread thread)} on a static log.
	 */
	public static void printStackTrace(Thread thread, PrintStream stream) {
		getMonitorSingleton().printStackTrace(thread, stream);
	}

	public static long getUpdateInterval() {
		return interval;
	}

	private static synchronized TraceLog getMonitorSingleton() {
		if (log == null) {
			log = new TraceLog();
			log.update(Thread.getAllStackTraces(), System.currentTimeMillis());
		}
		return log;
	}
}
