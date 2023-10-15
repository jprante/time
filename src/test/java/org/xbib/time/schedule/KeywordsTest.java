package org.xbib.time.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeywordsTest {
    private Keywords keywords;

    @BeforeEach
    public void before() {
        keywords = new Keywords();
    }

    @Test
    public void normalUse() {
        keywords.put("AAA", 1);
        keywords.put("BBB", 2);
        assertEquals(1, keywords.get("AAABBB", 0, 3));
        assertEquals(2, keywords.get("AAABBB", 3, 6));
    }

    @Test
    public void getNotPresent() {
        try {
            assertEquals(-1, keywords.get("CCC", 0, 3));
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getWrongAlphabet() {
        try {
            keywords.get("aaa", 0, 3);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getWrongLength() {
        try {
            keywords.get("aaa", 0, 1);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void putEmpty() {
        try {
            keywords.put("", 0);
            fail("Expected exception");
        } catch (StringIndexOutOfBoundsException e) {
            assertEquals("Index 0 out of bounds for length 0", e.getMessage());
        }
    }

    @Test
    public void putWrongLength() {
        try {
            keywords.put("A", 0);
            fail("Expected exception");
        } catch (StringIndexOutOfBoundsException e) {
            assertEquals("Index 1 out of bounds for length 1", e.getMessage());
        }
    }

    @Test
    public void putWrongAlphabet() {
        try {
            keywords.put("a", 0);
            fail("Expected exception");
        } catch (ArrayIndexOutOfBoundsException e) {
            assertEquals("Index 32 out of bounds for length 26", e.getMessage());
        }
    }
}
