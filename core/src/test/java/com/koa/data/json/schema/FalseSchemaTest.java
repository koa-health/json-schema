package com.koa.data.json.schema;

import org.everit.json.schema.FalseSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author erosb
 */
public class FalseSchemaTest {

    @Test public void alwaysFails() {
        TestSupport.failureOf(org.everit.json.schema.FalseSchema.builder())
                .input("whatever")
                .expectedKeyword("false")
                .expect();
    }

    @Test
    public void toStringTest() {
        assertEquals("false", FalseSchema.builder().build().toString());
    }
}
