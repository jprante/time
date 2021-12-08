package org.xbib.time.schedule;

import java.time.ZonedDateTime;
import java.util.concurrent.Callable;

public class Entry<T> {

    private final String name;

    private final CronExpression cronExpression;

    private final Callable<T> callable;

    private ZonedDateTime lastCalled;

    private ZonedDateTime nextCall;

    public Entry(String name, CronExpression cronExpression, Callable<T> callable) {
        this.name = name;
        this.cronExpression = cronExpression;
        this.callable = callable;
    }

    public String getName() {
        return name;
    }

    public CronExpression getCronExpression() {
        return cronExpression;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public void setLastCalled(ZonedDateTime lastCalled) {
        this.lastCalled = lastCalled;
        // heuristic, limit to 1 year ahead
        this.nextCall = cronExpression.nextExecution(lastCalled, lastCalled.plusYears(1));
    }

    public ZonedDateTime getLastCalled() {
        return lastCalled;
    }

    public ZonedDateTime getNextCall() {
        return nextCall;
    }

    @Override
    public String toString() {
        return "Entry[name=" + name + ", expression=" + cronExpression +
                ",callable= " + callable +
                ",lastcalled=" + lastCalled +
                ",nextcall=" + nextCall + "]";
    }
}
