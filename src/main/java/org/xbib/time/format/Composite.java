package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Composite implementation that merges other fields to create a full pattern.
 */
public class Composite implements PeriodPrinter, PeriodParser {

    private final PeriodPrinter[] iPrinters;

    private final PeriodParser[] iParsers;

    public Composite(List<Object> elementPairs) {
        List<PeriodPrinter> printerList = new ArrayList<>();
        List<PeriodParser> parserList = new ArrayList<>();
        decompose(elementPairs, printerList, parserList);
        if (printerList.isEmpty()) {
            iPrinters = null;
        } else {
            iPrinters = printerList.toArray(new PeriodPrinter[0]);
        }
        if (parserList.isEmpty()) {
            iParsers = null;
        } else {
            iParsers = parserList.toArray(new PeriodParser[0]);
        }
    }

    @Override
    public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
        int sum = 0;
        PeriodPrinter[] printers = iPrinters;
        for (int i = printers.length; sum < stopAt && --i >= 0; ) {
            sum += printers[i].countFieldsToPrint(period, Integer.MAX_VALUE, locale);
        }
        return sum;
    }

    @Override
    public int calculatePrintedLength(Period period, Locale locale) {
        int sum = 0;
        PeriodPrinter[] printers = iPrinters;
        for (int i = printers.length; --i >= 0; ) {
            sum += printers[i].calculatePrintedLength(period, locale);
        }
        return sum;
    }

    @Override
    public void printTo(StringBuilder buf, Period period, Locale locale) throws IOException {
        for (PeriodPrinter printer : iPrinters) {
            printer.printTo(buf, period, locale);
        }
    }

    @Override
    public void printTo(Writer out, Period period, Locale locale) throws IOException {
        for (PeriodPrinter printer : iPrinters) {
            printer.printTo(out, period, locale);
        }
    }

    @Override
    public int parseInto(PeriodAmount period, String periodStr, int pos, Locale locale) {
        int position = pos;
        PeriodParser[] parsers = iParsers;
        if (parsers == null) {
            throw new UnsupportedOperationException();
        }
        int len = parsers.length;
        for (int i = 0; i < len && position >= 0; i++) {
            position = parsers[i].parseInto(period, periodStr, position, locale);
        }
        return position;
    }

    private void decompose(List<Object> elementPairs, List<PeriodPrinter> printerList, List<PeriodParser> parserList) {
        int size = elementPairs.size();
        for (int i = 0; i < size; i += 2) {
            Object element = elementPairs.get(i);
            if (element instanceof PeriodPrinter) {
                if (element instanceof Composite) {
                    PeriodPrinter[] periodPrinters = ((Composite) element).iPrinters;
                    if (periodPrinters != null) {
                        Collections.addAll(printerList, periodPrinters);
                    }
                } else {
                    printerList.add((PeriodPrinter) element);
                }
            }
            element = elementPairs.get(i + 1);
            if (element instanceof PeriodParser) {
                if (element instanceof Composite) {
                    PeriodParser[] periodParsers = ((Composite) element).iParsers;
                    if (periodParsers != null) {
                        Collections.addAll(parserList, periodParsers);
                    }
                } else {
                    parserList.add((PeriodParser) element);
                }
            }
        }
    }
}
