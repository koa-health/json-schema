package com.koa.data.json.schema;


import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.InternalValidationException;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InternalValidationExceptionTest {

    @Test
    public void stackTraceShouldBeEmpty() {
        try {
            throw new org.everit.json.schema.InternalValidationException(BooleanSchema.INSTANCE, "message", "keyword", "#");
        } catch (ValidationException e) {
            assertEquals(0, e.getStackTrace().length);
        }
    }
}
