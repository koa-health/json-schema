package com.koa.data.json.schema.loader;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static com.koa.data.json.schema.JSONMatcher.sameJsonAs;
import static com.koa.data.json.schema.TestSupport.asStream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import com.koa.data.json.schema.ResourceLoader;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.SchemaLocation;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SpecificationVersion;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonPointerEvaluatorTest {

    private static final org.everit.json.schema.loader.JsonObject rootSchemaJson = JsonValueTest.withLs(org.everit.json.schema.loader.JsonValue.of(ResourceLoader.DEFAULT.readObj("testschemas.json")
            .getJSONObject("refPointerDerivatedFromPointer").toMap())).requireObject();

    private static final ResourceLoader LOADER = ResourceLoader.DEFAULT;

    @Test
    void sameDocumentSuccess() {
        org.everit.json.schema.loader.JsonPointerEvaluator pointer = org.everit.json.schema.loader.JsonPointerEvaluator.forDocument(rootSchemaJson, "#/definitions/Bar");
        org.everit.json.schema.loader.JsonObject actual = pointer.query().getQueryResult().requireObject();
        assertEquals("dummy schema at #/definitions/Bar", actual.require("description").requireString());
        assertEquals("http://localhost:1234/folder/", actual.ls.id.toString());
        assertEquals(new SchemaLocation(asList("definitions", "Bar")), actual.ls.pointerToCurrentObj);
    }

    @Test
    void sameDocumentNotFound() {
        Assertions.assertThrows(SchemaException.class, () -> {
            org.everit.json.schema.loader.JsonPointerEvaluator pointer = org.everit.json.schema.loader.JsonPointerEvaluator.forDocument(rootSchemaJson, "#/definitions/NotFound");
            org.everit.json.schema.loader.JsonObject actual = pointer.query().getQueryResult().requireObject();
            assertEquals("dummy schema at #/definitions/Bar", actual.require("description").requireString());
            assertEquals("http://localhost:1234/folder/", actual.ls.id.toString());
            assertEquals(new SchemaLocation(asList("definitions", "Bar")), actual.ls.pointerToCurrentObj);
        });
    }

    @Test
    void arrayIndexSuccess() {
        org.everit.json.schema.loader.JsonPointerEvaluator pointer = org.everit.json.schema.loader.JsonPointerEvaluator.forDocument(rootSchemaJson, "#/definitions/Array/0");
        org.everit.json.schema.loader.JsonObject actual = pointer.query().getQueryResult().requireObject();
        assertEquals("dummy schema in array", actual.require("description").requireString());
    }

    @Test
    void rootRefSuccess() {
        org.everit.json.schema.loader.JsonPointerEvaluator pointer = org.everit.json.schema.loader.JsonPointerEvaluator.forDocument(rootSchemaJson, "#");
        org.everit.json.schema.loader.JsonObject actual = pointer.query().getQueryResult().requireObject();
        assertSame(rootSchemaJson, actual);
    }

    @Test
    void escaping() {
        org.everit.json.schema.loader.JsonPointerEvaluator pointer = org.everit.json.schema.loader.JsonPointerEvaluator.forDocument(rootSchemaJson, "#/definitions/Escaping/sla~1sh/ti~0lde");
        org.everit.json.schema.loader.JsonObject actual = pointer.query().getQueryResult().requireObject();
        assertEquals("tiled", actual.require("description").requireString());
    }

    private org.everit.json.schema.loader.LoadingState createLoadingState(org.everit.json.schema.loader.SchemaClient schemaClient, String ref) {
        org.everit.json.schema.loader.LoaderConfig config = new org.everit.json.schema.loader.LoaderConfig(schemaClient, emptyMap(), SpecificationVersion.DRAFT_4, false);
        URI parentScopeId = null;
        Object rootSchemaJson = this.rootSchemaJson;
        HashMap<String, Object> schemaJson = new HashMap<>();
        schemaJson.put("$ref", ref);
        return new org.everit.json.schema.loader.LoadingState(config, new HashMap<>(), rootSchemaJson, schemaJson, parentScopeId, SchemaLocation.empty());
    }

    @Test
    void remoteDocumentSuccess() throws URISyntaxException {
        org.everit.json.schema.loader.SchemaClient schemaClient = mock(org.everit.json.schema.loader.SchemaClient.class);
        when(schemaClient.get("http://localhost:1234/hello")).thenReturn(rootSchemaJsonAsStream());
        org.everit.json.schema.loader.JsonPointerEvaluator pointer = org.everit.json.schema.loader.JsonPointerEvaluator
                .forURL(schemaClient, "http://localhost:1234/hello#/definitions/Bar",
                        createLoadingState(schemaClient, "#/definitions/Foo"));
        org.everit.json.schema.loader.JsonObject actual = pointer.query().getQueryResult().requireObject();
        assertEquals("dummy schema at #/definitions/Bar", actual.require("description").requireString());
        assertEquals("http://localhost:1234/folder/", actual.ls.id.toString());
        assertEquals(new SchemaLocation(new URI("http://localhost:1234/hello"), asList("definitions", "Bar")),
                actual.ls.pointerToCurrentObj);
    }
    
    @Test
    void remoteDocument_jsonParsingFailure() {
        org.everit.json.schema.loader.SchemaClient schemaClient = mock(org.everit.json.schema.loader.SchemaClient.class);
        when(schemaClient.get("http://localhost:1234/hello")).thenReturn(asStream("unparseable"));
        SchemaException actual = assertThrows(SchemaException.class,
            () -> org.everit.json.schema.loader.JsonPointerEvaluator.forURL(schemaClient, "http://localhost:1234/hello#/foo", createLoadingState(schemaClient, "")).query()
        );
        assertEquals("http://localhost:1234/hello", actual.getSchemaLocation());
    }

    @Test
    void schemaExceptionForInvalidURI() {
        try {
            org.everit.json.schema.loader.SchemaClient schemaClient = mock(SchemaClient.class);
            org.everit.json.schema.loader.JsonPointerEvaluator subject = org.everit.json.schema.loader.JsonPointerEvaluator.forURL(schemaClient, "||||",
                    createLoadingState(schemaClient, "#/definitions/Foo"));
            subject.query();
            fail("did not throw exception");
        } catch (SchemaException e) {
            assertThat(e.toJSON(), sameJsonAs(LOADER.readObj("pointer-eval-non-uri-failure.json")));
        }
    }

    protected InputStream rootSchemaJsonAsStream() {
        return asStream(new JSONObject(rootSchemaJson.toMap()).toString());
    }
}

