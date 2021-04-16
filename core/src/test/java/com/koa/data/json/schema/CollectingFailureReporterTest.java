package com.koa.data.json.schema;

import static java.util.Arrays.asList;
import static org.everit.json.schema.ValidationException.createWrappingException;
import static org.junit.jupiter.api.Assertions.*;

import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.CollectingFailureReporter;
import org.everit.json.schema.FalseSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class CollectingFailureReporterTest {

    public static final Runnable NOOP = () -> {
    };

    @Test
    public void noNewExceptions_returnsNull() {
        org.everit.json.schema.ValidationException actual = createSubject().inContextOfSchema(org.everit.json.schema.NullSchema.INSTANCE, NOOP);
        assertNull(actual);
    }

    @Test
    public void subSchemaIsNull() {
        assertThrows(NullPointerException.class, () -> {
            createSubject().inContextOfSchema(null, () -> {
            });
        });
    }

    @Test
    public void singleExceptionAdded_andReturned() {
        org.everit.json.schema.CollectingFailureReporter subject = createSubject();
        org.everit.json.schema.ValidationException entry = new org.everit.json.schema.ValidationException(org.everit.json.schema.NullSchema.INSTANCE, JSONObject.NULL.getClass(), "string");

        org.everit.json.schema.ValidationException actual = subject.inContextOfSchema(org.everit.json.schema.NullSchema.INSTANCE, () -> {
            subject.failure(entry);
        });

        assertSame(entry, actual);
        assertEquals(0, subject.failureCount());
    }

    @Test
    public void multipleFailures_areWrapped() {
        org.everit.json.schema.CollectingFailureReporter subject = createSubject();
        org.everit.json.schema.ValidationException entry1 = new org.everit.json.schema.ValidationException(FalseSchema.builder().build(), JSONObject.NULL.getClass(), "string");
        org.everit.json.schema.ValidationException entry2 = new org.everit.json.schema.ValidationException(org.everit.json.schema.NullSchema.INSTANCE, JSONObject.NULL.getClass(), "string");

        org.everit.json.schema.ValidationException expected = createWrappingException(org.everit.json.schema.NullSchema.INSTANCE, asList(entry1, entry2));

        ValidationException actual = subject.inContextOfSchema(NullSchema.INSTANCE, () -> {
            subject.failure(entry1);
            subject.failure(entry2);
        });

        assertEquals(expected, actual);
        assertEquals(0, subject.failureCount());
    }

    private org.everit.json.schema.CollectingFailureReporter createSubject() {
        return new org.everit.json.schema.CollectingFailureReporter(BooleanSchema.INSTANCE);
    }
}
