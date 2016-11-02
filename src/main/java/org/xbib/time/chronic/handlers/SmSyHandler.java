package org.xbib.time.chronic.handlers;

import org.xbib.time.chronic.Options;
import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.Token;
import org.xbib.time.chronic.tags.ScalarMonth;
import org.xbib.time.chronic.tags.ScalarYear;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class SmSyHandler implements IHandler {

    private static final Logger logger = Logger.getLogger(SmSyHandler.class.getName());

    @Override
    public Span handle(List<Token> tokens, Options options) {
        int month = tokens.get(0).getTag(ScalarMonth.class).getType();
        int year = tokens.get(1).getTag(ScalarYear.class).getType();
        Span span;
        try {
            ZonedDateTime start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, options.getZoneId());
            ZonedDateTime end = start.plus(1, ChronoUnit.MONTHS);
            span = new Span(start, end);
        } catch (IllegalArgumentException e) {
            logger.log(Level.FINE, e.getMessage(), e);
            span = null;
        }
        return span;
    }

}
