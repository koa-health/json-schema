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
import static org.hamcrest.MatcherAssert.assertThat;

import org.json.JSONObject;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class NullSchemaTest {

    @Test
    public void failure() {
        TestSupport.failureOf(NullSchema.builder())
                .expectedKeyword("type")
                .input("null")
                .expect();
    }

    @Test
    public void success() {
        JSONObject obj = new JSONObject("{\"a\" : null}");
        NullSchema.INSTANCE.validate(obj.get("a"));
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(NullSchema.class)
                .withRedefinedSuperclass()
                .withIgnoredFields("schemaLocation", "location")
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void toStringTest() {
        NullSchema subject = NullSchema.builder()
                .description("it can only be null")
                .build();
        assertThat(ResourceLoader.DEFAULT.readObj("tostring/null-schema.json"), sameJsonAs(new JSONObject(subject.toString())));
    }
}
