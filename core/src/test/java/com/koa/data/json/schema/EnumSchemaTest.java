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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.internal.JSONPrinter;
import org.json.JSONArray;
import org.json.JSONObject;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnumSchemaTest {

    private List<Object> possibleValues;

    @BeforeEach
    public void before() {
        possibleValues = new ArrayList<>();
        possibleValues.add(true);
        possibleValues.add("foo");
    }

    @Test
    public void failure() {
        TestSupport.failureOf(subject())
                .expectedPointer("#")
                .expectedKeyword("enum")
                .input(new JSONArray("[1]"))
                .expect();
    }

    private org.everit.json.schema.EnumSchema.Builder subject() {
        return (org.everit.json.schema.EnumSchema.Builder) org.everit.json.schema.EnumSchema.builder().possibleValues(possibleValues).title("my title");
    }

    @Test
    public void success() {
        possibleValues.add(new JSONArray());
        possibleValues.add(new JSONObject("{\"a\" : 0}"));
        org.everit.json.schema.EnumSchema subject = subject().build();
        subject.validate(true);
        subject.validate("foo");
        subject.validate(new JSONArray());
        subject.validate(new JSONObject("{\"a\" : 0}"));
    }

    @Test
    public void objectInArrayMatches() {
        JSONArray arr = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("a", true);
        arr.put(obj);
        possibleValues.add(arr);

        org.everit.json.schema.EnumSchema subject = subject().build();

        subject.validate(new JSONArray("[{\"a\":true}]"));
    }

    private Set<Object> asSet(final JSONArray array) {
        return IntStream.range(0, array.length())
                .mapToObj(array::get)
                .collect(Collectors.toSet());
    }

    @Test
    public void toStringTest() {
        StringWriter buffer = new StringWriter();
        subject().build().describeTo(new JSONPrinter(buffer));
        JSONObject actual = new JSONObject(buffer.getBuffer().toString());
        assertEquals(2, JSONObject.getNames(actual).length);
        JSONArray pv = new JSONArray(asList(true, "foo"));
        assertEquals(asSet(pv), asSet(actual.getJSONArray("enum")));
        assertEquals("my title", actual.get("title"));
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(org.everit.json.schema.EnumSchema.class)
                .withRedefinedSuperclass()
                .withIgnoredFields("schemaLocation", "location")
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void nullSuccess() {
        EnumSchema.builder().possibleValue(null).build().validate(JSONObject.NULL);
    }

}
