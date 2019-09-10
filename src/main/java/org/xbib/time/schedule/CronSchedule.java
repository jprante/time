package org.xbib.time.schedule;

import java.io.Closeable;
import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CronSchedule<T> implements Closeable {

    private final ScheduledExecutorService executor;

    private final List<Entry<T>> entries;

    private final int periodInMilliseconds;

    private ScheduledFuture<?> future;

    public CronSchedule(ScheduledExecutorService scheduledExecutorServices) {
        this(scheduledExecutorServices, 60000);
    }

    public CronSchedule(ScheduledExecutorService scheduledExecutorServices,
                        int periodInMilliseconds) {
        this.executor = scheduledExecutorServices;
        this.entries = new ArrayList<>();
        this.periodInMilliseconds = periodInMilliseconds;
    }

    public void add(String name, CronExpression expression, Callable<T> callable) {
        entries.add(new Entry<T>(name, expression, callable));
    }

    public void remove(String name) {
        entries.removeIf(entry -> name.equals(entry.getName()));
    }

    public  List<Entry<T>> getEntries() {
        return entries;
    }

    public void start() {
        long initialDelay = periodInMilliseconds - (Clock.systemDefaultZone().millis() % periodInMilliseconds);
        this.future = executor.scheduleAtFixedRate(CronSchedule.this::run,
                initialDelay, periodInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public void run() {
        run(ZonedDateTime.now());
    }

    public void run(ZonedDateTime time) {
        for (Entry<T> entry : entries) {
            if (entry.getCronExpression().matches(time)) {
                entry.setLastCalled(time);
                executor.submit(entry.getCallable());
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public String toString() {
        return entries.toString();
    }
}
