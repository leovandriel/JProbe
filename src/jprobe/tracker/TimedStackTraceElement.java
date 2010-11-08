package jprobe.tracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A stack trace element with an time field that indicates the earliest time
 * this element was running.
 */
public class TimedStackTraceElement {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");
	private StackTraceElement element;
	private long startMillis;

	public TimedStackTraceElement(String declaringClass, String methodName, String fileName, int lineNumber,
			long startMillis) {
		this(new StackTraceElement(declaringClass, methodName, fileName, lineNumber), startMillis);
	}

	public TimedStackTraceElement(StackTraceElement element, long startMillis) {
		this.element = element;
		this.startMillis = startMillis;
	}

	public StackTraceElement getStackTraceElement() {
		return element;
	}

	public long getStartMillis() {
		return startMillis;
	}

	public void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
	}

	public String getClassName() {
		return element.getClassName();
	}

	public String toString(long timeMillis) {
		return element.toString() + '[' + formatRelative(timeMillis - startMillis) + ']';
	}

	public String toString() {
		return element.toString() + '[' + formatAbsolute(startMillis) + ']';
	}

	private static String formatRelative(long deltaMillis) {
		String unit;
		if (deltaMillis < 1000) {
			unit = "";
		} else if (deltaMillis < 1000 * 60) {
			unit = "s";
			deltaMillis /= 1000;
		} else if (deltaMillis < 1000 * 60 * 60) {
			unit = "m";
			deltaMillis /= 1000 * 60;
		} else if (deltaMillis < 1000 * 60 * 60 * 24) {
			unit = "h";
			deltaMillis /= 1000 * 60 * 60;
		} else {
			unit = "d";
			deltaMillis /= 1000 * 60 * 60 * 24;
		}
		return deltaMillis + unit;
	}

	private static String formatAbsolute(long timeMillis) {
		return DATE_FORMAT.format(new Date(timeMillis));
	}
}
