package org.xbib.time.chronic.tags;

import org.xbib.time.chronic.Options;
import org.xbib.time.chronic.Token;

import java.util.regex.Pattern;

/**
 *
 */
public class ScalarDay extends Scalar {

    private static final Pattern DAY_PATTERN = Pattern.compile("^\\d\\d?$");

    public ScalarDay(Integer type) {
        super(type);
    }

    public static ScalarDay scan(Token token, Token postToken, Options options) {
        if (ScalarDay.DAY_PATTERN.matcher(token.getWord()).matches()) {
            int scalarValue = Integer.parseInt(token.getWord());
            if (!(scalarValue > 31 || (postToken != null && Scalar.TIMES.contains(postToken.getWord())))) {
                return new ScalarDay(scalarValue);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-day-" + getType();
    }
}
