package com.koa.data.json.schema;

import org.everit.json.schema.TrueSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author erosb
 */
public class TrueSchemaTest {

    @Test
    public void testToString() {
        assertEquals("true", TrueSchema.builder().build().toString());
    }

}
