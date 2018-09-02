package com.ab.calypsowatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class CalypsoWatcher {

	private EventBus eventBus;
	private static DirectoryEventWatcher dirWatcher;

	private TestSubscriber subscriber;
	private static int port = 9876;

	public static void main(String[] args) throws Exception {
		CalypsoWatcher watcher = new CalypsoWatcher();
		watcher.watch();
		startSocket();
	}

	private void watch() throws Exception {
		eventBus = new EventBus();
		dirWatcher = new DirectoryEventWatcher(eventBus);
		dirWatcher.start();

		subscriber = new TestSubscriber();
		eventBus.register(subscriber);
	}

	private static void startSocket() throws Exception {
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("SocketListenerThread").build();
		ExecutorService executor = Executors.newSingleThreadExecutor(namedThreadFactory);
		Future<String> future = executor.submit(new SocketListener(port));
		String ret = future.get();
		if (ret.equalsIgnoreCase("STOP")) {
			executor.shutdown();
			dirWatcher.stop();
			try {
				if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
			}
		}
	}

}