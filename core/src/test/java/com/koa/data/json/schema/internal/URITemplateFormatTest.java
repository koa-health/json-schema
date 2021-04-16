package com.koa.data.json.schema.internal;

import static com.koa.data.json.schema.internal.ValidatorTestSupport.assertFailure;
import static com.koa.data.json.schema.internal.ValidatorTestSupport.assertSuccess;

import org.everit.json.schema.internal.URITemplateFormatValidator;
import org.junit.jupiter.api.Test;

public class URITemplateFormatTest {

    private final org.everit.json.schema.internal.URITemplateFormatValidator subject = new URITemplateFormatValidator();

    @Test
    public void success() {
        assertSuccess("http://example.com/dictionary/{term:1}/{term}", subject);
    }

    @Test
    public void unclosedBracket() {
        assertFailure("http://example.com/dictionary/{term:1}/{term", subject,
                "[http://example.com/dictionary/{term:1}/{term] is not a valid URI template");
    }
}
