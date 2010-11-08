package jprobe.thread;

/**
 * This class provides a default abstract implementation of the ThreadNode
 * interface. It includes all shared functionality in ThreadLeaf and
 * ThreadBranch.
 * 
 * @see ThreadNode
 */
public abstract class AbstractThreadNode implements ThreadNode {
	public boolean doInterrupt() {
		try {
			interrupt();
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}

	protected abstract void interrupt();

	public String getFullName() {
		String parents = getPath();
		if (parents.length() > 0) {
			return parents + "." + getName();
		}
		return getName();
	}

	public ThreadBranch getParent() {
		ThreadGroup group = getThreadGroup();
		if (group != null) {
			return new ThreadBranch(group);
		}
		return null;
	}

	protected abstract ThreadGroup getThreadGroup();

	public String getPath() {
		ThreadBranch group = getParent();
		if (group != null) {
			return group.getFullName();
		}
		return "";
	}

	public ThreadBranch getRoot() {
		ThreadBranch parent = getParent();
		if (parent != null) {
			return parent.getRoot();
		}
		if (this instanceof ThreadBranch) {
			return (ThreadBranch) this;
		}
		return null;
	}

	public boolean isAccessible() {
		try {
			checkAccess();
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}

	protected abstract void checkAccess();

}
