package com.koa.data.json.schema.event;

import org.everit.json.schema.FalseSchema;
import org.everit.json.schema.TrueSchema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.event.ConditionalSchemaMismatchEvent;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ConditionalSchemaMismatchEventTest {

    @Test
    public void equalsVerifier() {
        ValidationException exc1 = new ValidationException(TrueSchema.INSTANCE, "message", "keyword", "#/location");
        ValidationException exc2 = new ValidationException(FalseSchema.INSTANCE, "message", "keyword", "#/loca/tion");
        EqualsVerifier.forClass(ConditionalSchemaMismatchEvent.class)
                .withNonnullFields("keyword", "schema", "instance", "failure")
                .withIgnoredFields("path")
                .withRedefinedSuperclass()
                .withPrefabValues(ValidationException.class, exc1, exc2)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

}