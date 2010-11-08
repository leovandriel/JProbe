package jprobe.tracker;

/**
 * Represents a timed stack trace into which new stack traces can be merged. By
 * repeatedly merging stack traces into this merge, the trace elements times
 * become an accurate representation of the running time of individual elements.
 */
public class TraceRecord {
	private TimedStackTraceElement[] timedTrace;

	public TraceRecord() {
		this.timedTrace = new TimedStackTraceElement[0];
	}

	public TraceRecord(StackTraceElement[] trace, long currentTime) {
		this.timedTrace = convertToTimed(trace, currentTime);
	}

	public synchronized TimedStackTraceElement[] getTrace() {
		return timedTrace;
	}

	public synchronized void merge(StackTraceElement[] trace, long currentTime) {
		TimedStackTraceElement[] newTimedElements = convertToTimed(trace, currentTime);
		mergeInto(timedTrace, newTimedElements);
		timedTrace = newTimedElements;
	}

	private static TimedStackTraceElement[] convertToTimed(StackTraceElement[] trace, long currentTime) {
		TimedStackTraceElement[] result = new TimedStackTraceElement[trace.length];
		for (int i = 0; i < trace.length; i++) {
			result[i] = new TimedStackTraceElement(trace[i], currentTime);
		}
		return result;
	}

	private static void mergeInto(TimedStackTraceElement[] previous, TimedStackTraceElement[] current) {
		for (int i = previous.length - 1, j = current.length - 1; i >= 0 && j >= 0; i--, j--) {
			if (!previous[i].getStackTraceElement().equals(current[j].getStackTraceElement())) {
				break;
			}
			current[j].setStartMillis(previous[i].getStartMillis());
		}
	}
}
