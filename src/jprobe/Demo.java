package jprobe;

import java.util.LinkedList;
import java.util.List;

import jprobe.monitor.ClassMatcher;
import jprobe.monitor.LogAction;
import jprobe.monitor.LoopHunter;
import jprobe.tracker.Tracker;

public class Demo {
	public static void main(String[] args) throws Throwable {
		LoopHunter hunter = new LoopHunter(new ClassMatcher(".*sf.*", 100000), new LogAction());
		hunter.start();
		List<Thread> threads = new LinkedList<Thread>();
		for (int i = 0; i < 1; i++) {
			threads.add(startWork(i));
		}
		print();
		for (int i = 0; i < 4; i++) {
			Thread.sleep(5000);
			// print();
		}
		for (Thread t : threads) {
			t.interrupt();
		}
		Thread.sleep(5000);
		print();
		System.out.println("Done!");
	}

	private static Thread startWork(int i) {
		Thread result = new Thread("Worker-" + i) {
			public void run() {
				go(1);
			}
		};
		result.start();
		return result;
	}

	private static void go(int depth) {
		Thread.yield();
		while (Math.random() > 0.05 * depth) {
			go(depth + 1);
		}
	}

	private static void print() {
		System.out.println();
		System.out.println("----------------------------------------------------------------");
		System.out.println();
		for (Thread thread : Thread.getAllStackTraces().keySet()) {
			Tracker.printStackTrace(thread, System.out);
			System.out.println();
		}
		System.out.println("----------------------------------------------------------------");
		System.out.println();
	}
}
