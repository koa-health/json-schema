package com.koa.data.json.schema.loader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ObjectSchema;
import com.koa.data.json.schema.ResourceLoader;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class DefinesPropertyTest {

    private static JSONObject ALL_SCHEMAS = ResourceLoader.DEFAULT.readObj("testschemas.json");

    private JSONObject get(final String schemaName) {
        return ALL_SCHEMAS.getJSONObject(schemaName);
    }

    @Test
    public void objectSchemaHasField() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("pointerResolution"));
        assertTrue(actual.definesProperty("#/rectangle"));
        assertTrue(actual.definesProperty("#/rectangle/a"));
        assertTrue(actual.definesProperty("#/rectangle/b"));

        assertFalse(actual.definesProperty("#/rectangle/c"));
        assertFalse(actual.definesProperty("#/rectangle/"));
        assertFalse(actual.definesProperty("#/"));
        assertFalse(actual.definesProperty("#/a"));
        assertFalse(actual.definesProperty("#"));
        assertFalse(actual.definesProperty("#/rectangle/a/d"));
    }

    @Test
    public void recursiveSchemaHasField() {
        Schema recursiveSchema = org.everit.json.schema.loader.SchemaLoader.load(get("recursiveSchema"));

        assertTrue(recursiveSchema.definesProperty("#/prop"));
        assertTrue(recursiveSchema.definesProperty("#/prop/subprop"));
        assertTrue(recursiveSchema.definesProperty("#/prop/subprop/subprop"));
        assertTrue(recursiveSchema.definesProperty("#/prop/subprop/subprop/subprop"));
    }

    @Test
    public void patternPropertiesHasField() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("patternProperties"));
        assertTrue(actual.definesProperty("#/a"));
        assertTrue(actual.definesProperty("#/aa"));
        assertTrue(actual.definesProperty("#/aaa"));
        assertTrue(actual.definesProperty("#/aaaa"));
        assertTrue(actual.definesProperty("#/aaaaa"));
        
        assertFalse(actual.definesProperty("b"));
    }

    @Test
    public void objectWithSchemaDep() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("objectWithSchemaDep"));
        assertTrue(actual.definesProperty("#/a"));
        assertTrue(actual.definesProperty("#/b"));

        assertFalse(actual.definesProperty("#/c"));
    }

    @Test
    public void objectWithSchemaRectangleDep() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("objectWithSchemaRectangleDep"));
        assertTrue(actual.definesProperty("#/d"));
        assertTrue(actual.definesProperty("#/rectangle/a"));
        assertTrue(actual.definesProperty("#/rectangle/b"));

        assertFalse(actual.definesProperty("#/c"));
        assertFalse(actual.definesProperty("#/d/c"));
        assertFalse(actual.definesProperty("#/rectangle/c"));
    }

    @Test
    public void objectEscape() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("objectEscape"));
        assertTrue(actual.definesProperty("#/a~0b"));
        assertTrue(actual.definesProperty("#/a~0b/c~1d"));

        assertFalse(actual.definesProperty("#/a~0b/c/d"));
    }

    @Test
    public void definesPropertyIfSubschemaMatchCountIsAcceptedByCriterion() {
        CombinedSchema subject = CombinedSchema.builder()
                .subschema(ObjectSchema.builder().addPropertySchema("a", BooleanSchema.INSTANCE).build())
                .subschema(ObjectSchema.builder().addPropertySchema("b", BooleanSchema.INSTANCE).build())
                .criterion((subschemaCount, matchingSubschemaCount) -> {
                    if (matchingSubschemaCount == 1 && subschemaCount == 2) {
                        // dummy exception
                        throw new ValidationException(Object.class, new Object());
                    }
                })
                .build();
        assertFalse(subject.definesProperty("a"));
    }

    @Test
    public void testOfTest() {
        ObjectSchema actual = (ObjectSchema) org.everit.json.schema.loader.SchemaLoader.load(get("patternPropsAndSchemaDeps"));
        JSONObject input = ResourceLoader.DEFAULT
                .readObj("objecttestcases.json")
                .getJSONObject("validOfPatternPropsAndSchemaDeps");
        actual.validate(input);
    }

    @Test
    public void patternPropsAndSchemaDefs() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("patternPropsAndSchemaDeps"));
        // Assert.assertTrue(actual.definesProperty("#/1stLevel"));
        // Assert.assertTrue(actual.definesProperty("#/1stLevel/2ndLevel"));
        assertTrue(actual.definesProperty("#/1stLevel/2ndLevel/3rdLev"));
        // Assert.assertTrue(actual.definesProperty("#/1stLevel/2ndLevel/3rdLevel/4thLevel"));
    }

}
