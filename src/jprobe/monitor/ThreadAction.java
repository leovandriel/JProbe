package jprobe.monitor;

/**
 * An action to be run for a specific thread.
 */
public interface ThreadAction {
	public void run(Thread thread);
}
