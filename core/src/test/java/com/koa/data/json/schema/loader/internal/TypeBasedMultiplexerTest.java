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
package com.koa.data.json.schema.loader.internal;

import org.everit.json.schema.SchemaException;
import org.everit.json.schema.loader.internal.ResolutionScopeChangeListener;
import org.everit.json.schema.loader.internal.TypeBasedMultiplexer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;

public class TypeBasedMultiplexerTest {

    @Test
    public void differentPortNum() {
        expectScopeChanges(objectWithId("otherschema.json"), "http://x.y.z:8080/otherschema.json",
                "http://x.y.z:8080/rootschema.json");
    }

    private URI uri(final String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void dispatchesIdChangeEvent() {
        JSONObject scopeChangingObj = objectWithId("changedId");
        org.everit.json.schema.loader.internal.TypeBasedMultiplexer subject =
                new org.everit.json.schema.loader.internal.TypeBasedMultiplexer(null, scopeChangingObj, uri("http://orig"));
        org.everit.json.schema.loader.internal.ResolutionScopeChangeListener mockListener = Mockito.mock(org.everit.json.schema.loader.internal.ResolutionScopeChangeListener.class);
        subject.addResolutionScopeChangeListener(mockListener);
        subject.ifObject().then(o -> {
        }).requireAny();
        Mockito.verify(mockListener).resolutionScopeChanged(
                Mockito.argThat(uriAsString("http://origchangedId")));
        Mockito.verify(mockListener).resolutionScopeChanged(uri("http://orig"));
    }

    private void expectScopeChanges(final JSONObject subjectOfMultiplexing, final String newScope,
            final String origScope) {
        org.everit.json.schema.loader.internal.TypeBasedMultiplexer subject = new org.everit.json.schema.loader.internal.TypeBasedMultiplexer(null, subjectOfMultiplexing,
                uri(origScope));
        org.everit.json.schema.loader.internal.ResolutionScopeChangeListener mockListener = Mockito.mock(org.everit.json.schema.loader.internal.ResolutionScopeChangeListener.class);
        subject.addResolutionScopeChangeListener(mockListener);
        subject.ifObject().then(o -> {
        }).requireAny();
        Mockito.verify(mockListener).resolutionScopeChanged(Mockito.argThat(uriAsString(newScope)));
        Mockito.verify(mockListener).resolutionScopeChanged(uri(origScope));
    }

    private Matcher<URI> uriAsString(final String uri) {
        return new TypeSafeMatcher<URI>() {

            @Override
            public void describeTo(final Description arg0) {
            }

            @Override
            protected boolean matchesSafely(final URI actual) {
                return actual.toString().equals(uri);
            }
        };
    }

    @Test
    public void fragmentIdOccurence() {
        JSONObject objWithFragment = objectWithId("#foo");
        expectScopeChanges(objWithFragment, "http://x.y.z/rootschema.json#foo",
                "http://x.y.z/rootschema.json");
    }

    @Test
    public void newRoot() {
        expectScopeChanges(objectWithId("http://otherserver.com"), "http://otherserver.com",
                "http://x.y.z:8080/rootschema.json");
    }

    @Test
    public void nonFragmentRelativePath() {
        expectScopeChanges(objectWithId("otherschema.json"), "http://x.y.z/otherschema.json",
                "http://x.y.z/rootschema.json");
    }

    private JSONObject objectWithId(final String idAttribute) {
        JSONObject scopeChangingObj = new JSONObject();
        scopeChangingObj.put("id", idAttribute);
        return scopeChangingObj;
    }

    @Test
    public void relpathThenFragment() {
        JSONObject outerObj = objectWithId("otherschema.json");
        JSONObject innerObj = objectWithId("#bar");
        outerObj.put("innerObj", innerObj);
        org.everit.json.schema.loader.internal.TypeBasedMultiplexer outerMultiplexer = new org.everit.json.schema.loader.internal.TypeBasedMultiplexer(null, outerObj,
                uri("http://x.y.z/rootschema.json"));
        org.everit.json.schema.loader.internal.ResolutionScopeChangeListener outerListener = Mockito.mock(org.everit.json.schema.loader.internal.ResolutionScopeChangeListener.class);
        org.everit.json.schema.loader.internal.ResolutionScopeChangeListener innerListener = Mockito.mock(ResolutionScopeChangeListener.class);
        outerMultiplexer.addResolutionScopeChangeListener(outerListener);
        outerMultiplexer
                .ifObject()
                .then(
                        obj -> {
                            org.everit.json.schema.loader.internal.TypeBasedMultiplexer innerMultiplexer =
                                    new org.everit.json.schema.loader.internal.TypeBasedMultiplexer(null, obj.get("innerObj"),
                                            uri("http://x.y.z/otherschema.json"));
                            innerMultiplexer.addResolutionScopeChangeListener(innerListener);
                            innerMultiplexer.ifObject().then(o -> {
                            }).requireAny();
                        }).requireAny();
        Mockito.verify(outerListener).resolutionScopeChanged(uri("http://x.y.z/otherschema.json"));
        Mockito.verify(innerListener).resolutionScopeChanged(uri("http://x.y.z/otherschema.json#bar"));
        Mockito.verify(innerListener).resolutionScopeChanged(uri("http://x.y.z/otherschema.json"));
        Mockito.verify(outerListener).resolutionScopeChanged(uri("http://x.y.z/rootschema.json"));
    }

    @Test
    public void relpathWithFragment() {
        expectScopeChanges(objectWithId("t/inner.json#a"), "http://x.y.z:8080/t/inner.json#a",
                "http://x.y.z:8080/rootschema.json");
    }

    @Test
    public void typeBasedMultiplexerFailure() {
        Assertions.assertThrows(SchemaException.class, () -> {
            new org.everit.json.schema.loader.internal.TypeBasedMultiplexer("foo")
                    .ifObject().then(o -> {
            })
                    .ifIs(JSONArray.class).then(o -> {
            })
                    .requireAny();
        });
    }

    @Test
    public void typeBasedMultiplexerTest() {
        new org.everit.json.schema.loader.internal.TypeBasedMultiplexer(new JSONObject())
                .ifObject().then(jsonObj -> {
        })
                .ifIs(JSONArray.class).then(jsonArr -> {
        })
                .orElse(obj -> {
                });

        new TypeBasedMultiplexer(new JSONObject())
                .ifObject().then(jsonObj -> {
        })
                .ifIs(JSONArray.class).then(jsonArr -> {
        })
                .requireAny();
    }

}