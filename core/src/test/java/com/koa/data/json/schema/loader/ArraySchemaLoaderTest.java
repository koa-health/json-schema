package com.koa.data.json.schema.loader;

import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.NullSchema;
import com.koa.data.json.schema.ResourceLoader;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.TrueSchema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.koa.data.json.schema.TestSupport.loadAsV6;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author erosb
 */
public class ArraySchemaLoaderTest {

    private static JSONObject ALL_SCHEMAS = ResourceLoader.DEFAULT.readObj("arraytestschemas.json");

    private static JSONObject get(final String schemaName) {
        return ALL_SCHEMAS.getJSONObject(schemaName);
    }

    @Test
    public void additionalItemSchema() {
        assertTrue(org.everit.json.schema.loader.SchemaLoader.load(get("additionalItemSchema")) instanceof ArraySchema);
    }

    @Test
    public void arrayByAdditionalItems() {
        ArraySchema actual = (ArraySchema) org.everit.json.schema.loader.SchemaLoader.load(get("arrayByAdditionalItems"));
        assertFalse(actual.requiresArray());
    }

    @Test
    public void arrayByItems() {
        ArraySchema actual = (ArraySchema) org.everit.json.schema.loader.SchemaLoader.load(get("arrayByItems"));
        assertNotNull(actual);
    }

    @Test
    public void arraySchema() {
        ArraySchema actual = (ArraySchema) org.everit.json.schema.loader.SchemaLoader.load(get("arraySchema"));
        assertNotNull(actual);
        assertEquals(2, actual.getMinItems().intValue());
        assertEquals(3, actual.getMaxItems().intValue());
        assertTrue(actual.needsUniqueItems());
        assertEquals(NullSchema.INSTANCE, actual.getAllItemSchema());
    }

    @Test
    public void invalidAdditionalItems() {
        Assertions.assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.loader.SchemaLoader.load(get("invalidAdditionalItems"));
        });
    }

    @Test
    public void invalidArrayItemSchema() {
        Assertions.assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.loader.SchemaLoader.load(get("invalidArrayItemSchema"));
        });
    }

    @Test
    public void invalidItemsArraySchema() {
        Assertions.assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.loader.SchemaLoader.load(get("invalidItemsArraySchema"));
        });
    }

    @Test
    public void v6LoaderSupportsContains() {
        ArraySchema result = (ArraySchema) loadAsV6(get("arrayWithContains"));
        assertNotNull(result.getContainedItemSchema());
    }

    @Test
    public void v4LoaderDoesNotSupportContains() {
        ArraySchema result = (ArraySchema) SchemaLoader.load(get("arrayWithContains"));
        assertNull(result.getContainedItemSchema());
    }

    @Test
    public void itemsCanBeBooleanInV6() {
        ArraySchema actual = (ArraySchema) loadAsV6(get("itemsAsBoolean"));
        assertEquals(TrueSchema.builder().build(), actual.getAllItemSchema());
    }

}
