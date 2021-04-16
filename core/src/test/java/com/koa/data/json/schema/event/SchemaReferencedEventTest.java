package com.koa.data.json.schema.event;

import org.everit.json.schema.event.SchemaReferencedEvent;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class SchemaReferencedEventTest {

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(SchemaReferencedEvent.class)
                .withNonnullFields("referredSchema", "schema", "instance")
                .withIgnoredFields("path")
                .withRedefinedSuperclass()
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }
}
