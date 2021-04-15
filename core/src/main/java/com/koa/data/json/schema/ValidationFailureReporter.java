package com.koa.data.json.schema;

import org.everit.json.schema.InternalValidationException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;

import static java.util.Objects.requireNonNull;

/**
 * Internal interface receiving validation failures. Implementations are supposed to throw or collect {@link org.everit.json.schema.ValidationException} instances.
 * <p>
 * The validation always happens in the context of some "current schema". This {@link org.everit.json.schema.Schema} instance will
 * be the {@link org.everit.json.schema.ValidationException#getViolatedSchema() violated schema} of the {@code ValidationException}s created.
 * </p>
 */
abstract class ValidationFailureReporter {

    protected org.everit.json.schema.Schema schema;

    ValidationFailureReporter(org.everit.json.schema.Schema schema) {
        this.schema = requireNonNull(schema, "schema cannot be null");
    }

    void failure(String message, String keyword) {
        failure(new org.everit.json.schema.InternalValidationException(schema, message, keyword, schema.getSchemaLocation()));
    }

    void failure(Class<?> expectedType, Object actualValue) {
        failure(new org.everit.json.schema.InternalValidationException(schema, expectedType, actualValue, "type", schema.getSchemaLocation()));
    }

    abstract void failure(org.everit.json.schema.ValidationException exc);

    ValidationException inContextOfSchema(org.everit.json.schema.Schema schema, Runnable task) {
        requireNonNull(schema, "schema cannot be null");
        Schema origSchema = this.schema;
        this.schema = schema;
        task.run();
        this.schema = origSchema;
        return null;
    }

    abstract void validationFinished();

    Object getState() {
        return null;
    }

    boolean isChanged(Object oldState) {
        return false;
    }
}
