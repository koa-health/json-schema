package com.koa.data.json.schema.internal;

import static com.koa.data.json.schema.internal.ValidatorTestSupport.assertFailure;
import static com.koa.data.json.schema.internal.ValidatorTestSupport.assertSuccess;

import org.everit.json.schema.internal.URIReferenceFormatValidator;
import org.junit.jupiter.api.Test;

public class URIReferenceFormatValidatorTest {

    private final org.everit.json.schema.internal.URIReferenceFormatValidator subject = new URIReferenceFormatValidator();

    @Test
    public void success() {
        assertSuccess("http://foo.bar/?baz=qux#quux", subject);
    }

    @Test
    public void protocolRelativeRef() {
        assertSuccess("//foo.bar/?baz=qux#quux", subject);
    }

    @Test
    public void pathSuccess() {
        assertSuccess("/abc", subject);
    }

    @Test
    public void illegalCharFailure() {
        assertFailure("\\\\WINDOWS\\fileshare", subject, "[\\\\WINDOWS\\fileshare] is not a valid URI reference");
    }

}
