package com.koa.data.json.schema;

import static com.koa.data.json.schema.ConditionalSchemaTest.MAX_LENGTH_STRING_SCHEMA;
import static com.koa.data.json.schema.ConditionalSchemaTest.MIN_LENGTH_STRING_SCHEMA;
import static com.koa.data.json.schema.ConditionalSchemaTest.PATTERN_STRING_SCHEMA;
import static org.everit.json.schema.event.ConditionalSchemaValidationEvent.Keyword.ELSE;
import static org.everit.json.schema.event.ConditionalSchemaValidationEvent.Keyword.IF;
import static org.everit.json.schema.event.ConditionalSchemaValidationEvent.Keyword.THEN;
import static org.mockito.Mockito.*;

import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.Validator;
import org.everit.json.schema.event.ConditionalSchemaMatchEvent;
import org.everit.json.schema.event.ConditionalSchemaMismatchEvent;
import org.everit.json.schema.event.ValidationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConditionalSchemaEventsTest {

    private org.everit.json.schema.ConditionalSchema schema = ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA)
            .thenSchema(MIN_LENGTH_STRING_SCHEMA)
            .elseSchema(MAX_LENGTH_STRING_SCHEMA).schemaLocation("#").build();

    ValidationListener listener = mock(ValidationListener.class);

    private org.everit.json.schema.ValidationFailureReporter reporter;

    @BeforeEach
    public void before() {
        reporter = new org.everit.json.schema.CollectingFailureReporter(schema);
    }

    private void validateInstance(String instance) {
        try {
            Validator.builder()
                    .withListener(listener)
                    .build()
                    .performValidation(schema, instance);
        } catch (org.everit.json.schema.ValidationException e) {
            // intentionally ignored
        }
    }

    @Test
    public void ifMatch_thenMatch() {
        String instance = "f###oo";
        validateInstance(instance);
        verify(listener).ifSchemaMatch(new ConditionalSchemaMatchEvent(schema, instance, IF));
        verify(listener).thenSchemaMatch(new ConditionalSchemaMatchEvent(schema, instance, THEN));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void ifMatch_thenMismatch() {
        String instance = "foo";
        validateInstance(instance);

        verify(listener).ifSchemaMatch(new ConditionalSchemaMatchEvent(schema, instance, IF));
        org.everit.json.schema.ValidationException failure = new org.everit.json.schema.InternalValidationException(MIN_LENGTH_STRING_SCHEMA,
                "expected minLength: 6, actual: 3", "minLength",
                "#/then");
        verify(listener).thenSchemaMismatch(new ConditionalSchemaMismatchEvent(schema, instance, THEN, failure));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void ifMismatch_elseMatch() {
        String instance = "boo";
        validateInstance(instance);

        org.everit.json.schema.ValidationException failure = new org.everit.json.schema.InternalValidationException(PATTERN_STRING_SCHEMA,
                "string [boo] does not match pattern f.*o", "pattern", "#/if");
        verify(listener).ifSchemaMismatch(new ConditionalSchemaMismatchEvent(schema, instance, IF, failure));
        verify(listener).elseSchemaMatch(new ConditionalSchemaMatchEvent(schema, instance, ELSE));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void ifMismatch_elseMismatch() {
        String instance = "booooooooooooo";
        validateInstance(instance);

        org.everit.json.schema.ValidationException ifFailure = new org.everit.json.schema.InternalValidationException(PATTERN_STRING_SCHEMA,
                "string [booooooooooooo] does not match pattern f.*o",
                "pattern", "#/if");
        verify(listener).ifSchemaMismatch(new ConditionalSchemaMismatchEvent(schema, instance, IF, ifFailure));
        ValidationException elseFailure = new org.everit.json.schema.InternalValidationException(MAX_LENGTH_STRING_SCHEMA,
                "expected maxLength: 4, actual: 14",
                "maxLength", "#/else");
        verify(listener).elseSchemaMismatch(new ConditionalSchemaMismatchEvent(schema, instance, ELSE, elseFailure));
        verifyNoMoreInteractions(listener);
    }

}
