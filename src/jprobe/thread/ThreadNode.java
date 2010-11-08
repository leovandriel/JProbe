package jprobe.thread;

/**
 * The thread package provides a more convenient way to access java Thread and
 * ThreadGroup objects, allowing easier tree traversal and exception free
 * functions.
 * 
 * This interface exposes the shared functions that both the TreeBranchs
 * (ThreadGroup) and TreeLeafs (Thread) implement.
 */
public interface ThreadNode {
	public boolean doInterrupt();

	public String getClassName();

	public String getFullName();

	public String getName();

	public ThreadBranch getParent();

	public String getPath();

	public int getPriority();

	public ThreadBranch getRoot();

	public boolean isAccessible();

	public boolean isDaemon();
}
