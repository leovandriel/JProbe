package jprobe.monitor;

import java.util.regex.Pattern;

import jprobe.tracker.TimedStackTraceElement;
import jprobe.tracker.Tracker;

public class ClassMatcher implements ThreadMatcher {
	private Pattern pattern;
	private int spanMillis;

	public ClassMatcher(String regex, int spanMillis) {
		this.pattern = Pattern.compile(regex);
		this.spanMillis = spanMillis;
	}

	public boolean matches(Thread thread) {
		TimedStackTraceElement[] elements = Tracker.getStackTrace(thread);
		long maxTime = System.currentTimeMillis() - spanMillis;
		for (int i = 0; i < elements.length; i++) {
			if (matches(elements[i], maxTime)) {
				return true;
			}
		}
		return false;
	}

	private boolean matches(TimedStackTraceElement element, long maxTime) {
		if (!pattern.matcher(element.getClassName()).matches()) {
			return false;
		}
		if (element.getStartMillis() > maxTime) {
			return false;
		}
		return true;
	}
}
