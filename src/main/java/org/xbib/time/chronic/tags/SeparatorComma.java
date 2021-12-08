package org.xbib.time.chronic.tags;

import org.xbib.time.chronic.Options;
import org.xbib.time.chronic.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 */
public class SeparatorComma extends Separator {

    private static final Pattern COMMA_PATTERN = Pattern.compile("^,$");

    private SeparatorComma(SeparatorType type) {
        super(type);
    }

    public static SeparatorComma scan(Token token, Options options) {
        Map<Pattern, SeparatorType> scanner = new HashMap<>();
        scanner.put(SeparatorComma.COMMA_PATTERN, SeparatorType.COMMA);
        for (Map.Entry<Pattern, SeparatorType> entry : scanner.entrySet()) {
            Pattern scannerItem = entry.getKey();
            if (scannerItem.matcher(token.getWord()).matches()) {
                return new SeparatorComma(scanner.get(scannerItem));
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-comma";
    }

}
