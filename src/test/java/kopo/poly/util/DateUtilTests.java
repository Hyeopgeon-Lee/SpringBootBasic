package kopo.poly.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilTests {
    @Test
    void acceptsLongTimestampAndIntegerTimestamp() {
        assertEquals("2023", DateUtil.getLongDateTime(1700000000L, "yyyy"));
        assertEquals("2023", DateUtil.getLongDateTime(1700000000, "yyyy"));
    }

    @Test
    void toleratesOptionalMissingSunrise() {
        assertEquals("-", DateUtil.getLongDateTime((Object) null));
    }

    @Test
    void rejectsInvalidTimestampType() {
        assertThrows(IllegalArgumentException.class,
                () -> DateUtil.getLongDateTime("not-a-timestamp", "yyyy"));
    }
}
