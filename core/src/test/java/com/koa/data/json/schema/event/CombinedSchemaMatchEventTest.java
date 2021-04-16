package com.koa.data.json.schema.event;

import org.everit.json.schema.event.CombinedSchemaMatchEvent;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CombinedSchemaMatchEventTest {

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(CombinedSchemaMatchEvent.class)
                .withNonnullFields("subSchema", "schema", "instance")
                .withIgnoredFields("path")
                .withRedefinedSuperclass()
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

}
