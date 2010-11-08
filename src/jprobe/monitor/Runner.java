package jprobe.monitor;

public class Runner {
	private Thread thread;
	private String name;
	private boolean isDaemon;
	private Runnable target;

	public Runner(String name, boolean isDaemon, Runnable target) {
		this.name = name;
		this.isDaemon = isDaemon;
		this.target = target;
	}

	public synchronized void start() {
		if (thread != null) {
			throw new RuntimeException("Runner thread already/still running.");
		}
		thread = new Thread(name) {
			public void run() {
				try {
					target.run();
				} finally {
					synchronized (Runner.this) {
						thread = null;
					}
				}
			}
		};
		thread.setDaemon(isDaemon);
		thread.start();
	}

	public synchronized void stop() {
		if (thread != null) {
			thread.interrupt();
		}
	}
}
