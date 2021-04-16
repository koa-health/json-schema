package com.koa.data.json.schema.loader;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.loader.AdjacentSchemaExtractionState;
import org.everit.json.schema.loader.ExtractionResult;
import org.everit.json.schema.loader.JsonValue;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

public class AdjacentSchemaExtractionStateTest {

    @Test
    public void testReduce() {
        org.everit.json.schema.loader.AdjacentSchemaExtractionState original = new org.everit.json.schema.loader.AdjacentSchemaExtractionState(org.everit.json.schema.loader.JsonValue.of(ImmutableMap.builder()
                .put("const", "2")
                .put("minimum", 1)
                .build()
        ).requireObject());
        ConstSchema.ConstSchemaBuilder schemaBuilder = ConstSchema.builder().permittedValue("2");

        org.everit.json.schema.loader.AdjacentSchemaExtractionState actual = original.reduce(new org.everit.json.schema.loader.ExtractionResult("const", asList(schemaBuilder)));

        assertEquals(singleton(schemaBuilder), actual.extractedSchemaBuilders());
        assertEquals(singleton("minimum"), actual.projectedSchemaJson().keySet());
    }
}
