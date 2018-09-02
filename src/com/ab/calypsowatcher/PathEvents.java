package com.ab.calypsowatcher;

import com.ab.calypsowatcher.PathEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathEvents {

    private final List<PathEvent> pathEvents = new ArrayList<>();
    private final Path watchedDirectory;
    private final boolean isValid;

    PathEvents(boolean valid, Path watchedDirectory) {
        isValid = valid;
        this.watchedDirectory = watchedDirectory;
    }

  
    public boolean isValid(){
        return isValid;
    }


    public Path getWatchedDirectory(){
        return watchedDirectory;
    }

  
    public List<PathEvent> getEvents() {
        return Collections.unmodifiableList(pathEvents);
    }

    public void add(PathEvent pathEvent) {
        pathEvents.add(pathEvent);
    }
}
