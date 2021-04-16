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

import static java.util.Collections.emptyMap;
import static com.koa.data.json.schema.TestSupport.buildWithLocation;
import static org.junit.jupiter.api.Assertions.*;

import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.EmptySchema;
import org.everit.json.schema.ObjectComparator;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.ReferenceSchema.Builder;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class ReferenceSchemaTest {

    public static final ImmutableMap<String, Object> UNPROC_PROPS = ImmutableMap.of("unproc", true);

    @Test
    public void constructorMustRunOnlyOnce() {
        Builder builder = org.everit.json.schema.ReferenceSchema.builder();
        assertSame(builder.build(), builder.build());
    }

    @Test
    public void setterShouldWorkOnlyOnce() {
        assertThrows(IllegalStateException.class, () -> {
            org.everit.json.schema.ReferenceSchema subject = org.everit.json.schema.ReferenceSchema.builder().build();
            subject.setReferredSchema(org.everit.json.schema.BooleanSchema.INSTANCE);
            subject.setReferredSchema(org.everit.json.schema.BooleanSchema.INSTANCE);
        });
    }

    @Test
    public void validationShouldDelegateToReferredSchema() {
        org.everit.json.schema.ReferenceSchema subject = org.everit.json.schema.ReferenceSchema.builder().build();
        org.everit.json.schema.BooleanSchema referredSchema = buildWithLocation(BooleanSchema.builder());
        subject.setReferredSchema(referredSchema);
        TestSupport.failureOf(subject)
                .input("asd")
                .expectedViolatedSchema(referredSchema)
                .expectedKeyword("type")
                .expect();
    }

    @Test
    public void validateThrowsExc_IfNoReferredSchemaIsSet() {
        assertThrows(IllegalStateException.class, () -> {
            org.everit.json.schema.ReferenceSchema subject = org.everit.json.schema.ReferenceSchema.builder().build();
            subject.validate(null);
        });
    }

    @Test
    public void definesPropertyThrowsExc_IfNoReferredSchemaIsSet() {
        assertThrows(IllegalStateException.class, () -> {
            org.everit.json.schema.ReferenceSchema subject = org.everit.json.schema.ReferenceSchema.builder().build();
            subject.definesProperty("propName");
        });
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(org.everit.json.schema.ReferenceSchema.class)
                .withRedefinedSuperclass()
                .withIgnoredFields("schemaLocation", "location")
                //there are specifically some non final fields for loading of recursive schemas
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void unprocessedPropertiesBeforeSettingRefSchema() {
        org.everit.json.schema.ReferenceSchema subject = org.everit.json.schema.ReferenceSchema.builder()
                .unprocessedProperties(UNPROC_PROPS)
                .refValue("#/pointer")
                .build();
        subject.setReferredSchema(org.everit.json.schema.EmptySchema.builder().build());
        assertEquals(UNPROC_PROPS, subject.getUnprocessedProperties());
    }

    @Test
    public void unprocessedPropertiesAfterSettingRefSchema() {
        org.everit.json.schema.ReferenceSchema subject = org.everit.json.schema.ReferenceSchema.builder()
                .refValue("#/pointer")
                .unprocessedProperties(UNPROC_PROPS)
                .build();
        subject.setReferredSchema(org.everit.json.schema.EmptySchema.builder().build());
        assertEquals(UNPROC_PROPS, subject.getUnprocessedProperties());
    }

    @Test
    public void unprocessedPropertiesAfterBuild() {
        Builder builder = org.everit.json.schema.ReferenceSchema.builder()
                .refValue("#/pointer");
        org.everit.json.schema.ReferenceSchema subject = builder
                .build();
        subject.setReferredSchema(org.everit.json.schema.EmptySchema.builder().build());
        builder.unprocessedProperties(UNPROC_PROPS);
        assertEquals(UNPROC_PROPS, subject.getUnprocessedProperties());
    }

    @Test
    public void emptyUnprocessedProperties() {
        org.everit.json.schema.ReferenceSchema subject = ReferenceSchema.builder()
                .refValue("#/pointer")
                .build();
        subject.setReferredSchema(EmptySchema.builder().build());
        assertEquals(emptyMap(), subject.getUnprocessedProperties());
    }

    @Test
    public void toStringTest() {
        JSONObject rawSchemaJson = ResourceLoader.DEFAULT.readObj("tostring/ref.json");
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertTrue(ObjectComparator.deepEquals(rawSchemaJson.query("/properties"),
                new JSONObject(actual).query("/properties")));
    }

}
