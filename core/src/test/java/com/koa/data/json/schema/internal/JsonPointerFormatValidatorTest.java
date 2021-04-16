package com.koa.data.json.schema.internal;

import static java.lang.String.format;
import static com.koa.data.json.schema.internal.ValidatorTestSupport.assertSuccess;

import org.everit.json.schema.internal.JsonPointerFormatValidator;
import org.junit.jupiter.api.Test;

public class JsonPointerFormatValidatorTest {

    private final org.everit.json.schema.internal.JsonPointerFormatValidator subject = new JsonPointerFormatValidator();

    @Test
    public void stringSuccess() {
        assertSuccess("/hello", subject);
    }

    @Test
    public void root() {
        assertSuccess("/", subject);
    }

    @Test
    public void emptyStringIsValid() {
        assertSuccess("", subject);
    }

    @Test
    public void illegalLeadingCharFailure() {
        assertFailure("aaa");
    }

    @Test
    public void invalidTildeEscape() {
        assertFailure("/~asd");
    }

    @Test
    public void invalidEscapeNum() {
        assertFailure("/~2");
    }

    @Test
    public void trailingTilde() {
        assertFailure("/foo/bar~");
    }

    @Test
    public void uriFragment() {
        assertFailure("#/");
    }

    private void assertFailure(String input) {
        ValidatorTestSupport.assertFailure(input, subject, format("[%s] is not a valid JSON pointer", input));
    }

}
