package com.ab.calypsowatcher;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

public class TestSubscriber {
    List<PathEvents> pathEvents = new ArrayList<>();

    
    @Subscribe
    public void handlePathEvents(PathEvents ppathEvents) {
        pathEvents.add(ppathEvents);
        
    }

}
