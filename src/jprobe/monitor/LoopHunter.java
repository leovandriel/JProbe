package jprobe.monitor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jprobe.tracker.ThreadReference;

/**
 * Looks for thread that have gone haywire, as defined by a trace filter. Upon
 * finding such a thread, a set thread action is run. This happens only ones per
 * thread.
 */
public class LoopHunter {
	private static final long DEFAULT_INTERVAL = 10000;
	private ThreadMatcher matcher;
	private ThreadAction action;
	private long intervalMillis;
	private Set<ThreadReference> reportedThreads = new HashSet<ThreadReference>();
	private Runner huntRunner = new Runner("Loop Hunter", true, getRunnable());

	public LoopHunter(ThreadMatcher filter, ThreadAction action) {
		this(filter, action, DEFAULT_INTERVAL);
	}

	public LoopHunter(ThreadMatcher filter, ThreadAction action, long intervalMillis) {
		this.matcher = filter;
		this.action = action;
		this.intervalMillis = intervalMillis;
	}

	public void start() {
		huntRunner.start();
	}

	public void stop() {
		huntRunner.stop();
	}

	private void hunt() {
		reportedThreads.remove(new ThreadReference(null));
		Map<Thread, StackTraceElement[]> tracePerThread = Thread.getAllStackTraces();
		for (Thread thread : tracePerThread.keySet()) {
			if (matcher.matches(thread) && reportedThreads.add(new ThreadReference(thread))) {
				action.run(thread);
			}
		}
	}

	private Runnable getRunnable() {
		return new Runnable() {
			public void run() {
				for (;;) {
					try {
						Thread.sleep(intervalMillis);
					} catch (InterruptedException e) {
						break;
					}
					hunt();
				}
			}
		};
	}
}
