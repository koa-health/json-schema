package com.koa.data.json.schema.loader;

import com.koa.data.json.schema.ResourceLoader;
import com.koa.data.json.schema.TestSupport;
import org.everit.json.schema.*;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author erosb
 */
public class ObjectSchemaLoaderTest {

    private static JSONObject ALL_SCHEMAS = ResourceLoader.DEFAULT.readObj("objecttestschemas.json");

    private static JSONObject get(final String schemaName) {
        return ALL_SCHEMAS.getJSONObject(schemaName);
    }

    @Test
    public void objectSchema() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("objectSchema"));
        assertNotNull(actual);
        Map<String, Schema> propertySchemas = actual.getPropertySchemas();
        assertEquals(2, propertySchemas.size());
        assertEquals(BooleanSchema.INSTANCE, propertySchemas.get("boolProp"));
        assertFalse(actual.permitsAdditionalProperties());
        assertEquals(2, actual.getRequiredProperties().size());
        assertEquals(2, actual.getMinProperties().intValue());
        assertEquals(3, actual.getMaxProperties().intValue());
    }

    @Test
    public void objectInvalidAdditionalProperties() {
        assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.loader.SchemaLoader.load(get("objectInvalidAdditionalProperties"));
        });
    }

    @Test
    public void objectWithAdditionalPropSchema() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("objectWithAdditionalPropSchema"));
        assertEquals(BooleanSchema.INSTANCE, actual.getSchemaOfAdditionalProperties());
    }

    @Test
    public void objectWithPropDep() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("objectWithPropDep"));
        assertEquals(1, actual.getPropertyDependencies().get("isIndividual").size());
    }

    @Test
    public void objectWithSchemaDep() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("objectWithSchemaDep"));
        assertEquals(1, actual.getSchemaDependencies().size());
    }

    @Test
    public void patternProperties() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("patternProperties"));
        assertNotNull(actual);
        assertEquals(2, actual.getPatternProperties().size());
    }

    @Test
    public void invalidDependency() {
        assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.loader.SchemaLoader.load(get("invalidDependency"));
        });
    }

    @Test
    public void emptyDependencyList() {
        org.everit.json.schema.loader.SchemaLoader.load(get("emptyDependencyList"));
    }

    @Test
    public void invalidRequired() {
        SchemaException thrown = assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.loader.SchemaLoader.load(get("invalidRequired"));
        });
        assertEquals("#/required/1: expected type: String, found: JsonArray", thrown.getMessage());
    }

    @Test
    public void booleanDependency() {
        ObjectSchema actual = (ObjectSchema) TestSupport.loadAsV6(get("booleanDependencies"));
        assertEquals(actual.getSchemaDependencies().get("foo"), TrueSchema.builder().build());
    }

    @Test
    public void properyNamesV6() {
        ObjectSchema actual = (ObjectSchema) TestSupport.loadAsV6(get("propertyNames"));
        assertNotNull(actual.getPropertyNameSchema());
    }

    @Test
    public void properyNamesV4() {
        JSONObject rawSchema = get("propertyNames");
        rawSchema.put("type", "object");
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(rawSchema);
        assertNull(actual.getPropertyNameSchema());
    }

}
