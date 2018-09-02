package com.ab.calypsowatcher;

import com.google.common.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryEventWatcher {

    private FutureTask<Integer> watchTask;
    private EventBus eventBus;
    private WatchService watchService;
    private volatile boolean keepWatching = true;
    private final Map<WatchKey,Path> keyPaths = new ConcurrentHashMap<>(); 


    DirectoryEventWatcher(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus);
    }

    public void start() throws IOException {
        initWatchService();
        registerDirectories();
        createWatchTask();
        startWatching();
    }


    public boolean isRunning() {
        return watchTask != null && !watchTask.isDone();
    }

 
    public void stop() {
        keepWatching = false;
    }

   Integer getEventCount() {
        try {
            return watchTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private void createWatchTask() {
        watchTask = new FutureTask<>(new Callable<Integer>() {
            private int totalEventCount;

            @Override
            public Integer call() throws Exception {
                while (keepWatching) {
                    WatchKey watchKey = watchService.poll(10, TimeUnit.SECONDS);
                    if (watchKey != null) {
                        List<WatchEvent<?>> events = watchKey.pollEvents();
                        Path watched = (Path) watchKey.watchable();
                        PathEvents pathEvents = new PathEvents(watchKey.isValid(), watched);
                        for (WatchEvent event : events) {
                            pathEvents.add(new PathEvent((Path) event.context(), event.kind()));
                            totalEventCount++;
                        }
                        watchKey.reset();
                        eventBus.post(pathEvents);
                    }
                }
                return totalEventCount;
            }
        });
    }

    private void startWatching() {
        new Thread(null, watchTask, "WatcherThread").start();
    }

    private void registerDirectories() throws IOException {
    	Path p = new File("C:\\suhas").toPath();
	    WatchKey key = p.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
	    keyPaths.put(key, p);
    }

    private WatchService initWatchService() throws IOException {
        if (watchService == null) {
            watchService = FileSystems.getDefault().newWatchService();
        }
        return watchService;
    }
    
}
