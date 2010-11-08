package jprobe.monitor;


public interface ThreadMatcher {
	boolean matches(Thread thread);
}
