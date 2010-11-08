package jprobe.tracker;

import java.lang.ref.WeakReference;

/**
 * Weak reference to a thread for using a thread as key in a hash map. Both
 * {@link #equals(Object)} and {@link #hashCode()} have the same result when
 * applied to the referenced thread itself.
 */
public class ThreadReference {
	private WeakReference<Thread> reference;

	public ThreadReference(Thread thread) {
		this.reference = new WeakReference<Thread>(thread);
	}

	public Thread get() {
		return reference.get();
	}

	@Override
	public int hashCode() {
		Thread thread = reference.get();
		if (thread == null) {
			return 0;
		}
		return thread.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Thread thread = reference.get();
		Thread other = ((ThreadReference) obj).reference.get();
		if (thread == null) {
			if (other != null)
				return false;
		} else if (!thread.equals(other))
			return false;
		return true;
	}
}
