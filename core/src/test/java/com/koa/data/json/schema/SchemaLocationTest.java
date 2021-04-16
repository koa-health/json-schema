package com.koa.data.json.schema;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.everit.json.schema.SchemaLocation;
import org.junit.jupiter.api.Test;

public class SchemaLocationTest {

    private static URI uri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addPointerSegment() {
        org.everit.json.schema.SchemaLocation underTest = org.everit.json.schema.SchemaLocation.empty();
        org.everit.json.schema.SchemaLocation actual = underTest.addPointerSegment("key");
        assertEquals("#/key", actual.toString());
        assertEquals("#", underTest.toString());
    }

    @Test
    public void toString_noURI() {
        org.everit.json.schema.SchemaLocation underTest = new org.everit.json.schema.SchemaLocation(null, asList("a", "b/b", "c~c"));
        assertEquals("#/a/b~1b/c~0c", underTest.toString());
    }

    @Test
    public void toString_noURI_noPointer() {
        assertEquals("#", org.everit.json.schema.SchemaLocation.empty().toString());
    }

    @Test
    public void toString_uriWithTrailingHashmark() {
        org.everit.json.schema.SchemaLocation underTest = new org.everit.json.schema.SchemaLocation(uri("http://example.org/asd#"), singletonList("key"));
        assertEquals("http://example.org/asd#/key", underTest.toString());
    }

    @Test
    public void toString_uriAndPointer() {
        org.everit.json.schema.SchemaLocation underTest = new org.everit.json.schema.SchemaLocation(uri("http://example.com/hello"), singletonList("key"));
        assertEquals("http://example.com/hello#/key", underTest.toString());
    }

    @Test
    public void toString_uri_noPointer() {
        org.everit.json.schema.SchemaLocation underTest = new org.everit.json.schema.SchemaLocation(uri("http://example.com/hello"), emptyList());
        assertEquals("http://example.com/hello", underTest.toString());
    }

    @Test
    public void parseURI_null() {
        assertThrows(NullPointerException.class, () -> {
            org.everit.json.schema.SchemaLocation.parseURI(null);
        });
    }

    @Test
    public void parseURI_noHashmark() {
        org.everit.json.schema.SchemaLocation actual = org.everit.json.schema.SchemaLocation.parseURI("http://example.org");
        assertEquals(new org.everit.json.schema.SchemaLocation(uri("http://example.org"), emptyList()), actual);
    }

    @Test
    public void parseURI_emptyFragment() {
        org.everit.json.schema.SchemaLocation actual = org.everit.json.schema.SchemaLocation.parseURI("http://example.org#");
        assertEquals(new org.everit.json.schema.SchemaLocation(uri("http://example.org#"), emptyList()), actual);
    }

    @Test
    public void parseURI_singleSegmentPointer() {
        org.everit.json.schema.SchemaLocation actual = org.everit.json.schema.SchemaLocation.parseURI("http://example.org#/key");
        org.everit.json.schema.SchemaLocation expected = new org.everit.json.schema.SchemaLocation(uri("http://example.org"), new ArrayList<>(asList("key")));
        assertEquals(expected, actual);
    }

    @Test
    public void parseURI_onlyPointer() {
        org.everit.json.schema.SchemaLocation actual = org.everit.json.schema.SchemaLocation.parseURI("#/key");
        org.everit.json.schema.SchemaLocation expected = new org.everit.json.schema.SchemaLocation(null, new ArrayList<>(asList("key")));
        assertEquals(expected, actual);
    }

    @Test
    public void parseURI_multiSegmentPointer() {
        org.everit.json.schema.SchemaLocation actual = org.everit.json.schema.SchemaLocation.parseURI("http://example.org#/key1/key2");
        assertEquals(new org.everit.json.schema.SchemaLocation(uri("http://example.org"), asList("key1", "key2")), actual);
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(SchemaLocation.class)
                .withRedefinedSuperclass()
                .withNonnullFields("pointerToLocation")
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

}
