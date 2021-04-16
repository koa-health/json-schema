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
package com.koa.data.json.schema.loader;

import org.everit.json.schema.FormatValidator;
import com.koa.data.json.schema.ResourceLoader;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CustomFormatValidatorTest {

    private final ResourceLoader loader = ResourceLoader.DEFAULT;

    static class EvenCharNumValidator implements FormatValidator {

        @Override
        public Optional<String> validate(final String subject) {
            if (subject.length() % 2 == 0) {
                return Optional.empty();
            } else {
                return Optional.of(String.format("the length of srtring [%s] is odd", subject));
            }
        }

        @Override
        public String formatName() {
            return "evenlength";
        }
    }

    @Test
    public void test() {
        org.everit.json.schema.loader.SchemaLoader schemaLoader = org.everit.json.schema.loader.SchemaLoader.builder()
                .schemaJson(baseSchemaJson())
                .addFormatValidator("evenlength", new EvenCharNumValidator())
                .build();
        try {
            schemaLoader.load().build().validate(loader.readObj("customformat-data.json"));
            Assertions.fail("did not throw exception");
        } catch (ValidationException ve) {
        }
    }

    @Test
    public void nameOverride() {
        JSONObject rawSchemaJson = baseSchemaJson();
        JSONObject idPropSchema = (JSONObject) rawSchemaJson.query("/properties/id");
        idPropSchema.put("format", "somethingelse");
        org.everit.json.schema.loader.SchemaLoader schemaLoader = org.everit.json.schema.loader.SchemaLoader.builder()
                .schemaJson(rawSchemaJson)
                .addFormatValidator("somethingelse", new EvenCharNumValidator())
                .build();
        Object actual = fetchFormatValueFromOutputJson(schemaLoader);
        Assertions.assertEquals("somethingelse", actual);
    }

    private Object fetchFormatValueFromOutputJson(org.everit.json.schema.loader.SchemaLoader schemaLoader) {
        return new JSONObject(schemaLoader.load().build().toString())
                .query("/properties/id/format");
    }

    private JSONObject baseSchemaJson() {
        return loader.readObj("customformat-schema.json");
    }

    @Test
    public void formatValidatorWithoutExplicitName() {
        org.everit.json.schema.loader.SchemaLoader schemaLoader = SchemaLoader.builder()
                .schemaJson(baseSchemaJson())
                .addFormatValidator(new EvenCharNumValidator())
                .build();
        Object actual = fetchFormatValueFromOutputJson(schemaLoader);
        Assertions.assertEquals("evenlength", actual);
    }

}
