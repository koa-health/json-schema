package com.koa.data.json.schema.event;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ConditionalSchemaMatchEventTest {

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(ConditionalSchemaMatchEvent.class)
                .withNonnullFields("keyword", "schema", "instance")
                .withIgnoredFields("path")
                .withRedefinedSuperclass()
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

}
