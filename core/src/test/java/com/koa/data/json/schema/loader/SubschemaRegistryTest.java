package com.koa.data.json.schema.loader;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.koa.data.json.schema.ResourceLoader;
import org.everit.json.schema.SchemaLocation;
import org.everit.json.schema.loader.SpecificationVersion;
import org.everit.json.schema.loader.internal.DefaultSchemaClient;
import org.junit.jupiter.api.Test;

public class SubschemaRegistryTest {

    static final org.everit.json.schema.loader.LoaderConfig CONFIG = new org.everit.json.schema.loader.LoaderConfig(new DefaultSchemaClient(), emptyMap(), SpecificationVersion.DRAFT_6, false);

    @Test
    public void emptySchemaContainsNoElems() {
        org.everit.json.schema.loader.JsonValue obj = org.everit.json.schema.loader.JsonValue.of(emptyMap());
        new org.everit.json.schema.loader.LoadingState(CONFIG, emptyMap(), obj, obj, null, SchemaLocation.empty());

        org.everit.json.schema.loader.SubschemaRegistry registry = new org.everit.json.schema.loader.SubschemaRegistry(obj);

        assertEquals(0, registry.storage.size());
    }

    @Test
    public void topLevelIdIsRecognized() {
        org.everit.json.schema.loader.JsonValue obj = org.everit.json.schema.loader.JsonValue.of(ResourceLoader.DEFAULT.readObj("testschemas.json").getJSONObject("schemaWithIdV6"));
        new org.everit.json.schema.loader.LoadingState(CONFIG, emptyMap(), obj, obj, null, SchemaLocation.empty());

        org.everit.json.schema.loader.SubschemaRegistry registry = new org.everit.json.schema.loader.SubschemaRegistry(obj);

        org.everit.json.schema.loader.JsonObject actual = registry.getById("http://example.org/schema/");
        assertSame(obj, actual);
    }

    @Test
    public void childInObjById_isRecognized() {
        org.everit.json.schema.loader.JsonValue obj = org.everit.json.schema.loader.JsonValue.of(ResourceLoader.DEFAULT.readObj("ref-lookup-tests.json"));
        new org.everit.json.schema.loader.LoadingState(CONFIG, emptyMap(), obj, obj, null, SchemaLocation.empty());

        org.everit.json.schema.loader.SubschemaRegistry registry = new org.everit.json.schema.loader.SubschemaRegistry(obj);

        org.everit.json.schema.loader.JsonObject actual = registry.getById("has-id");
        org.everit.json.schema.loader.JsonValue expected = obj.requireObject().require("definitions").requireObject().require("HasId");
        assertEquals(expected.unwrap(), actual.unwrap());
    }

    @Test
    public void childInArrayById_isRecognized() {
        org.everit.json.schema.loader.JsonValue obj = org.everit.json.schema.loader.JsonValue.of(ResourceLoader.DEFAULT.readObj("ref-lookup-tests.json"));
        new org.everit.json.schema.loader.LoadingState(CONFIG, emptyMap(), obj, obj, null, SchemaLocation.empty());

        org.everit.json.schema.loader.SubschemaRegistry registry = new org.everit.json.schema.loader.SubschemaRegistry(obj);

        org.everit.json.schema.loader.JsonObject actual = registry.getById("all-of-part-0");
        org.everit.json.schema.loader.JsonValue expected = obj.requireObject().require("definitions").requireObject().require("someAllOf")
            .requireObject().require("allOf").requireArray().at(0);
        assertEquals(expected.unwrap(), actual.unwrap());
    }


}
