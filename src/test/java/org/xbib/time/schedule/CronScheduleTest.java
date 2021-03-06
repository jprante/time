package org.xbib.time.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CronScheduleTest {

    private CronSchedule<Void> schedule;

    private ScheduledExecutorService executor;

    @BeforeEach
    public void before() {
        executor = Executors.newScheduledThreadPool(1);
    }

    @Test
    public void runMinutes() throws InterruptedException {
        schedule = new CronSchedule<>(executor, 60000);
        final AtomicBoolean run = new AtomicBoolean(false);
        schedule.add("test", CronExpression.parser()
                        .parse("* * * * * *"),
                () -> {
                    run.set(true);
                    return null;
                });
        assertFalse(run.get());
        schedule.start();
        Thread.sleep(TimeUnit.MINUTES.toMillis(2));
        assertTrue(run.get());
    }

    @Test
    public void runSeconds() throws Exception {
        schedule = new CronSchedule<>(executor, 1000);
        final AtomicBoolean run = new AtomicBoolean(false);
        schedule.add("test", CronExpression.parser()
                        .withSecondsField(true).parse("* * * * * *"),
                () -> {
                    run.set(true);
                    return null;
                });
        assertFalse(run.get());
        schedule.start();
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        assertTrue(run.get());
    }

    @Test
    public void removeOne() throws Exception {
        schedule = new CronSchedule<>(executor, 1000);
        final Multiset<String> counts = HashMultiset.create();
        Callable<Void> a = () -> {
            counts.add("a");
            return null;
        };
        Callable<Void> b = () -> {
            counts.add("b");
            return null;
        };
        CronExpression expression = CronExpression.parse("* * * * *");
        schedule.add("1", expression, a);
        schedule.add("2", expression, b);
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(1, counts.count("b"));
        schedule.remove("1");
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(2, counts.count("b"));
    }

    @Test
    public void removeAllForExpression() throws Exception {
        schedule = new CronSchedule<>(executor, 1000);
        final Multiset<String> counts = HashMultiset.create();
        Callable<Void> a = () -> {
            counts.add("a");
            return null;
        };
        Callable<Void> b = () -> {
            counts.add("b");
            return null;
        };
        CronExpression expression = CronExpression.parse("* * * * *");
        schedule.add("a", expression, a);
        schedule.add("b", expression, b);
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(1, counts.count("b"));
        schedule.remove("a");
        schedule.remove("b");
        runAndWait();
        assertEquals(1, counts.count("a"));
        assertEquals(1, counts.count("b"));
    }

    @AfterEach
    public void after() throws IOException {
        if (schedule != null) {
            schedule.close();
        }
    }

    private void runAndWait() throws InterruptedException {
        schedule.run();
        Thread.sleep(10);
    }
}
