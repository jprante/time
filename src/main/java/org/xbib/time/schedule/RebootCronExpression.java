package org.xbib.time.schedule;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Matches once only.
 */
public final class RebootCronExpression extends CronExpression {

    private final AtomicBoolean matchOnce;

    public RebootCronExpression() {
        matchOnce = new AtomicBoolean(true);
    }

    @Override
    public boolean matches(ZonedDateTime t) {
        return matchOnce.getAndSet(false);
    }

    @Override
    public ZonedDateTime nextExecution(ZonedDateTime from, ZonedDateTime to) {
        return null;
    }
}
