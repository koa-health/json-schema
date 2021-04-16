package com.koa.data.json.schema.loader;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * @author erosb
 */
public class JsonArrayTest {

    @Test
    public void testForEach() {
        JSONObject rawObj = new JSONObject();
        org.everit.json.schema.loader.JsonArray subject = JsonValueTest.withLs(new org.everit.json.schema.loader.JsonArray(asList(true, rawObj))).requireArray();
        org.everit.json.schema.loader.JsonArrayIterator iterator = mock(org.everit.json.schema.loader.JsonArrayIterator.class);
        subject.forEach(iterator);
        verify(iterator).apply(0, org.everit.json.schema.loader.JsonValue.of(true));
        verify(iterator).apply(1, org.everit.json.schema.loader.JsonValue.of(rawObj));
    }
}
