package jprobe.tracker;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A collection of trace merges from which timed stack traces can be extracted.
 * For accurate trace times, this log needs to be updated frequently.
 */
public class TraceLog {
	private Map<ThreadReference, TraceRecord> recordPerThread = new LinkedHashMap<ThreadReference, TraceRecord>();

	/**
	 * Updates the log based on a new set of stack traces for a given time.
	 */
	public synchronized void update(Map<Thread, StackTraceElement[]> tracePerThread, long timeMillis) {
		recordPerThread = merge(recordPerThread, tracePerThread, timeMillis);
	}

	private static Map<ThreadReference, TraceRecord> merge(Map<ThreadReference, TraceRecord> mergePerThread,
			Map<Thread, StackTraceElement[]> tracePerThread, long timeMillis) {
		Map<ThreadReference, TraceRecord> result = new LinkedHashMap<ThreadReference, TraceRecord>();
		for (Entry<Thread, StackTraceElement[]> e : tracePerThread.entrySet()) {
			ThreadReference reference = new ThreadReference(e.getKey());
			TraceRecord monitor = mergePerThread.get(reference);
			if (monitor == null) {
				monitor = new TraceRecord(e.getValue(), timeMillis);
			} else {
				monitor.merge(e.getValue(), timeMillis);
			}
			result.put(reference, monitor);
		}
		return result;
	}

	/**
	 * Returns a timed stack trace for all live threads, similar to
	 * {@link Thread#getAllStackTraces()}.
	 */
	public synchronized Map<Thread, TimedStackTraceElement[]> getAllStackTraces() {
		Map<Thread, TimedStackTraceElement[]> result = new LinkedHashMap<Thread, TimedStackTraceElement[]>();
		for (Entry<ThreadReference, TraceRecord> e : recordPerThread.entrySet()) {
			result.put(e.getKey().get(), e.getValue().getTrace());
		}
		return result;
	}

	/**
	 * Returns a timed stack trace for the given thread, similar to
	 * {@link Thread#getStackTrace()}.
	 */
	public synchronized TimedStackTraceElement[] getStackTrace(Thread thread) {
		ThreadReference reference = new ThreadReference(thread);
		TraceRecord merge = recordPerThread.get(reference);
		if (merge == null) {
			merge = new TraceRecord(thread.getStackTrace(), System.currentTimeMillis());
		}
		return merge.getTrace();
	}

	/**
	 * Prints out a timed stack trace for the given thread to the print stream.
	 */
	public synchronized void printStackTrace(Thread thread, PrintStream stream) {
		synchronized (stream) {
			long time = System.currentTimeMillis();
			stream.println(thread);
			TimedStackTraceElement[] elements = getStackTrace(thread);
			for (int i = 0; i < elements.length; i++) {
				stream.println("\tat " + elements[i].toString(time));
			}
		}
	}
}
