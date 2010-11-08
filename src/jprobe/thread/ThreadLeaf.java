package jprobe.thread;

import java.lang.Thread.State;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class represents a leaf in the thread tree, thereby wrapping
 * java.lang.Thread.
 * 
 * @see ThreadNode
 */
public class ThreadLeaf extends AbstractThreadNode {
	private Thread thread;

	public ThreadLeaf(Thread thread) {
		this.thread = thread;
	}

	public String getClassName() {
		return thread.getClass().getName();
	}

	public int getId() {
		return (int) thread.getId();
	}

	public String getName() {
		return thread.getName();
	}

	public int getPriority() {
		return thread.getPriority();
	}

	public List<StackTraceElement> getStackList() {
		return Arrays.asList(thread.getStackTrace());
	}

	public State getState() {
		return thread.getState();
	}

	public boolean isAlive() {
		return thread.isAlive();
	}

	public boolean isDaemon() {
		return thread.isDaemon();
	}

	public boolean isInterrupted() {
		return thread.isInterrupted();
	}

	public Thread getThread() {
		return thread;
	}

	public static ThreadLeaf getCurrent() {
		return new ThreadLeaf(Thread.currentThread());
	}

	public static ThreadLeaf getLeaf(int id) {
		Map<Thread, StackTraceElement[]> traceMap = Thread.getAllStackTraces();
		for (Thread thread : traceMap.keySet()) {
			if (thread.getId() == id) {
				return new ThreadLeaf(thread);
			}
		}
		return null;
	}

	@Override
	protected void checkAccess() {
		thread.checkAccess();
	}

	@Override
	protected void interrupt() {
		thread.interrupt();
	}

	@Override
	protected ThreadGroup getThreadGroup() {
		return thread.getThreadGroup();
	}
}
