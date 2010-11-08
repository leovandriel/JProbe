package jprobe.thread;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a branch in the Thread tree, thereby wrapping
 * java.util.ThreadGroup.
 * 
 * @see ThreadNode
 */
public class ThreadBranch extends AbstractThreadNode {
	private ThreadGroup group;

	public ThreadBranch(ThreadGroup group) {
		this.group = group;
	}

	public String getClassName() {
		return group.getClass().getName();
	}

	public List<ThreadBranch> getGroupList() {
		List<ThreadBranch> result = new LinkedList<ThreadBranch>();
		ThreadGroup[] groupArray = new ThreadGroup[group.activeGroupCount() * 2];
		int groupCount = -1;
		try {
			groupCount = group.enumerate(groupArray, false);
		} catch (SecurityException e) {
		}
		for (int i = 0; i < groupCount; i++) {
			result.add(new ThreadBranch(groupArray[i]));
		}
		return result;
	}

	public String getName() {
		return group.getName();
	}

	public int getPriority() {
		return group.getMaxPriority();
	}

	public List<ThreadLeaf> getThreadList() {
		List<ThreadLeaf> result = new LinkedList<ThreadLeaf>();
		Thread[] threadArray = new Thread[group.activeCount() * 2];
		int threadCount = -1;
		try {
			threadCount = group.enumerate(threadArray, false);
		} catch (SecurityException e) {
		}
		for (int i = 0; i < threadCount; i++) {
			result.add(new ThreadLeaf(threadArray[i]));
		}
		return result;
	}

	public boolean isDaemon() {
		return group.isDaemon();
	}

	public boolean isDestroyed() {
		return group.isDestroyed();
	}

	public static ThreadBranch getCurrent() {
		return ThreadLeaf.getCurrent().getParent();
	}

	public static ThreadBranch getBranch(String name) {
		Map<Thread, StackTraceElement[]> traceMap = Thread.getAllStackTraces();
		for (Thread thread : traceMap.keySet()) {
			if (name.equals(thread.getThreadGroup().getName())) {
				return new ThreadBranch(thread.getThreadGroup());
			}
		}
		return null;
	}

	@Override
	protected void checkAccess() {
		group.checkAccess();
	}

	@Override
	protected void interrupt() {
		group.interrupt();
	}

	@Override
	protected ThreadGroup getThreadGroup() {
		return group.getParent();
	}
}
