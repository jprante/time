package org.xbib.time.chronic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.xbib.time.chronic.tags.Scalar;
import org.xbib.time.chronic.tags.StringTag;

public class TokenTest {

    @Test
    public void testToken() {
        Token token = new Token("foo");
        assertEquals(0, token.getTags().size());
        assertFalse(token.isTagged());
        token.tag(new StringTag("mytag"));
        assertEquals(1, token.getTags().size());
        assertTrue(token.isTagged());
        assertEquals(StringTag.class, token.getTag(StringTag.class).getClass());
        token.tag(new Scalar(5));
        assertEquals(2, token.getTags().size());
        token.untag(StringTag.class);
        assertEquals(1, token.getTags().size());
        assertEquals("foo", token.getWord());
    }
}
