/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.koa.data.json.schema;

import static com.koa.data.json.schema.JSONMatcher.sameJsonAs;
import static com.koa.data.json.schema.TestSupport.buildWithLocation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.FalseSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.TrueSchema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ArraySchemaTest {

    private static final ResourceLoader loader = ResourceLoader.DEFAULT;

    private static final JSONObject ARRAYS = loader.readObj("arraytestcases.json");

    @Test
    public void additionalItemsSchema() {
        org.everit.json.schema.ArraySchema.builder()
                .addItemSchema(org.everit.json.schema.BooleanSchema.INSTANCE)
                .schemaOfAdditionalItems(org.everit.json.schema.NullSchema.INSTANCE)
                .build().validate(ARRAYS.get("additionalItemsSchema"));
    }

    @Test
    public void ignoreAdditionalItemsIfNoItemsSchema() {
        org.everit.json.schema.ArraySchema.builder()
            .schemaOfAdditionalItems(FalseSchema.INSTANCE)
            .build().validate(ARRAYS.get("noItemSchema"));
    }

    @Test
    public void additionalItemsSchemaFailure() {
        org.everit.json.schema.NullSchema nullSchema = buildWithLocation(org.everit.json.schema.NullSchema.builder());
        org.everit.json.schema.ArraySchema subject = buildWithLocation(
                org.everit.json.schema.ArraySchema.builder()
                        .addItemSchema(buildWithLocation(org.everit.json.schema.BooleanSchema.builder()))
                        .schemaOfAdditionalItems(nullSchema)
        );
        TestSupport.failureOf(subject)
                .expectedViolatedSchema(nullSchema)
                .expectedPointer("#/2")
                //                 .expectedKeyword("additionalItems")
                .input(ARRAYS.get("additionalItemsSchemaFailure"))
                .expect();
    }

    @Test
    public void booleanItems() {
        org.everit.json.schema.ArraySchema subject = org.everit.json.schema.ArraySchema.builder().allItemSchema(org.everit.json.schema.BooleanSchema.INSTANCE).build();
        TestSupport.expectFailure(subject, org.everit.json.schema.BooleanSchema.INSTANCE, "#/2", ARRAYS.get("boolArrFailure"));
    }

    @Test
    public void doesNotRequireExplicitArray() {
        org.everit.json.schema.ArraySchema.builder()
                .requiresArray(false)
                .uniqueItems(true)
                .build().validate(ARRAYS.get("doesNotRequireExplicitArray"));
    }

    @Test
    public void maxItems() {
        org.everit.json.schema.ArraySchema subject = buildWithLocation(org.everit.json.schema.ArraySchema.builder().maxItems(0));
        TestSupport.failureOf(subject)
                .subject(subject)
                .expectedPointer("#")
                .expectedKeyword("maxItems")
                .expectedMessageFragment("expected maximum item count: 0, found: 1")
                .input(ARRAYS.get("onlyOneItem"))
                .expect();
    }

    @Test
    public void minItems() {
        org.everit.json.schema.ArraySchema subject = buildWithLocation(org.everit.json.schema.ArraySchema.builder().minItems(2));
        TestSupport.failureOf(subject)
                .expectedPointer("#")
                .expectedKeyword("minItems")
                .input(ARRAYS.get("onlyOneItem"))
                .expect();
    }

    @Test
    public void noAdditionalItems() {
        org.everit.json.schema.ArraySchema subject = org.everit.json.schema.ArraySchema.builder()
                .additionalItems(false)
                .addItemSchema(org.everit.json.schema.BooleanSchema.INSTANCE)
                .addItemSchema(org.everit.json.schema.NullSchema.INSTANCE)
                .build();
        TestSupport.expectFailure(subject, "#", ARRAYS.get("twoItemTupleWithAdditional"));
    }

    @Test
    public void noItemSchema() {
        org.everit.json.schema.ArraySchema.builder().build().validate(ARRAYS.get("noItemSchema"));
    }

    @Test
    public void nonUniqueArrayOfArrays() {
        org.everit.json.schema.ArraySchema subject = buildWithLocation(org.everit.json.schema.ArraySchema.builder().uniqueItems(true));
        TestSupport.failureOf(subject)
                .expectedPointer("#")
                .expectedKeyword("uniqueItems")
                .input(ARRAYS.get("nonUniqueArrayOfArrays"))
                .expect();
    }

    @Test
    public void tupleAndListFailure() {
        assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.ArraySchema.builder().addItemSchema(org.everit.json.schema.BooleanSchema.INSTANCE).allItemSchema(org.everit.json.schema.NullSchema.INSTANCE)
                    .build();
        });
    }

    @Test
    public void tupleWithOneItem() {
        org.everit.json.schema.BooleanSchema boolSchema = buildWithLocation(BooleanSchema.builder());
        org.everit.json.schema.ArraySchema subject = buildWithLocation(org.everit.json.schema.ArraySchema.builder().addItemSchema(boolSchema));
        TestSupport.failureOf(subject)
                .expectedViolatedSchema(boolSchema)
                .expectedPointer("#/0")
                .input(ARRAYS.get("tupleWithOneItem"))
                .expect();
    }

    @Test
    public void subjectHasLessElemsThanTupleEntries() {
        org.everit.json.schema.ArraySchema subject = buildWithLocation(org.everit.json.schema.ArraySchema.builder()
                .addItemSchema(org.everit.json.schema.NullSchema.INSTANCE)
                .addItemSchema(TrueSchema.INSTANCE));
        subject.validate(ARRAYS.get("subjectHasLessElemsThanTupleEntries"));
    }

    @Test
    public void typeFailure() {
        TestSupport.failureOf(org.everit.json.schema.ArraySchema.builder())
                .expectedKeyword("type")
                .input(true)
                .expect();
    }

    @Test
    public void uniqueItemsObjectViolation() {
        org.everit.json.schema.ArraySchema subject = org.everit.json.schema.ArraySchema.builder().uniqueItems(true).build();
        TestSupport.expectFailure(subject, "#", ARRAYS.get("nonUniqueObjects"));
    }

    @Test
    public void uniqueItemsViolation() {
        org.everit.json.schema.ArraySchema subject = org.everit.json.schema.ArraySchema.builder().uniqueItems(true).build();
        TestSupport.expectFailure(subject, "#", ARRAYS.get("nonUniqueItems"));
    }

    @Test
    public void uniqueItemsWithSameToString() {
        org.everit.json.schema.ArraySchema.builder().uniqueItems(true).build()
                .validate(ARRAYS.get("uniqueItemsWithSameToString"));
    }

    @Test
    public void uniqueObjectValues() {
        org.everit.json.schema.ArraySchema.builder().uniqueItems(true).build()
                .validate(ARRAYS.get("uniqueObjectValues"));
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(org.everit.json.schema.ArraySchema.class)
                .withRedefinedSuperclass()
                .withIgnoredFields("schemaLocation", "location")
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void toStringTest() {
        JSONObject rawSchemaJson = loader.readObj("tostring/arrayschema-list.json");
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toStringAdditionalItems() {
        JSONObject rawSchemaJson = loader.readObj("tostring/arrayschema-list.json");
        rawSchemaJson.remove("items");
        rawSchemaJson.put("additionalItems", false);
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertFalse(new JSONObject(actual).getBoolean("additionalItems"));
    }

    @Test
    public void toStringNoExplicitType() {
        JSONObject rawSchemaJson = loader.readObj("tostring/arrayschema-list.json");
        rawSchemaJson.remove("type");
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toStringTupleSchema() {
        JSONObject rawSchemaJson = loader.readObj("tostring/arrayschema-tuple.json");
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toStringContains() {
        JSONObject rawSchemaJson = loader.readObj("tostring/arrayschema-contains.json");
        String actual = SchemaLoader.builder()
                .draftV6Support()
                .schemaJson(rawSchemaJson)
                .build()
                .load()
                .build()
                .toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void containedItemSchemaFailure() {
        TestSupport.failureOf(org.everit.json.schema.ArraySchema.builder()
                .containsItemSchema(org.everit.json.schema.NullSchema.INSTANCE))
                .expectedKeyword("contains")
                .expectedMessageFragment("expected at least one array item to match 'contains' schema")
                .input(ARRAYS.get("onlyOneItem"))
                .expect();
    }

    @Test
    public void containedItemSchemaEmptyArr() {
        TestSupport.failureOf(org.everit.json.schema.ArraySchema.builder()
                .containsItemSchema(NullSchema.INSTANCE))
                .expectedKeyword("contains")
                .expectedMessageFragment("expected at least one array item to match 'contains' schema")
                .input(ARRAYS.get("emptyArray"))
                .expect();
    }

    @Test
    public void requiresArray_nullable() {
        org.everit.json.schema.ArraySchema subject = ArraySchema.builder().requiresArray(true).nullable(true).build();
        subject.validate(JSONObject.NULL);
    }

}
