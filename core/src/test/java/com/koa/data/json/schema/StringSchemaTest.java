package com.koa.data.json.schema;

import static com.koa.data.json.schema.JSONMatcher.sameJsonAs;
import static com.koa.data.json.schema.TestSupport.buildWithLocation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.regexp.RE2JRegexpFactory;
import org.json.JSONObject;

import com.google.re2j.Pattern;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class StringSchemaTest {

    private static org.everit.json.schema.Schema loadWithNullableSupport(JSONObject rawSchemaJson) {
        return SchemaLoader.builder().nullableSupport(true).schemaJson(rawSchemaJson).build().load().build();
    }

    @Test
    public void formatFailure() {
        org.everit.json.schema.StringSchema subject = buildWithLocation(org.everit.json.schema.StringSchema.builder()
                .formatValidator(subj -> Optional.of("violation")));
        TestSupport.failureOf(subject)
                .expectedKeyword("format")
                .input("string")
                .expect();
    }

    @Test
    public void formatSuccess() {
        org.everit.json.schema.StringSchema subject = org.everit.json.schema.StringSchema.builder().formatValidator(subj -> Optional.empty()).build();
        subject.validate("string");
    }

    @Test
    public void maxLength() {
        org.everit.json.schema.StringSchema subject = buildWithLocation(org.everit.json.schema.StringSchema.builder().maxLength(3));
        TestSupport.failureOf(subject)
                .expectedKeyword("maxLength")
                .input("foobar")
                .expect();
    }

    @Test
    public void minLength() {
        org.everit.json.schema.StringSchema subject = buildWithLocation(org.everit.json.schema.StringSchema.builder().minLength(2));
        TestSupport.failureOf(subject)
                .expectedKeyword("minLength")
                .input("a")
                .expect();
    }

    @Test
    public void multipleViolations() {
        try {
            org.everit.json.schema.StringSchema.builder().minLength(3).maxLength(1).pattern("^b.*").build().validate("ab");
            fail();
        } catch (org.everit.json.schema.ValidationException e) {
            assertEquals(3, e.getCausingExceptions().size());
        }
    }

    @Test
    public void notRequiresString() {
        org.everit.json.schema.StringSchema.builder().requiresString(false).build().validate(2);
    }

    @Test
    public void patternFailure() {
        org.everit.json.schema.StringSchema subject = buildWithLocation(org.everit.json.schema.StringSchema.builder().pattern("^a*$"));
        TestSupport.failureOf(subject).expectedKeyword("pattern").input("abc").expect();
    }

    @Test
    public void patternSuccess() {
        org.everit.json.schema.StringSchema.builder().pattern("^a*$").build().validate("aaaa");
    }

    @Test
    public void success() {
        org.everit.json.schema.StringSchema.builder().build().validate("foo");
    }

    @Test
    public void typeFailure() {
        TestSupport.failureOf(org.everit.json.schema.StringSchema.builder())
                .expectedKeyword("type")
                .input(null)
                .expect();
    }

    @Test
    public void issue38Pattern() {
        assertThrows(ValidationException.class, () -> {
            org.everit.json.schema.StringSchema.builder().requiresString(true).pattern("\\+?\\d+").build().validate("aaa");
        });
    }

    @Test
    public void equalsVerifier() {
        EqualsVerifier.forClass(org.everit.json.schema.StringSchema.class)
                .withRedefinedSuperclass()
                .withIgnoredFields("schemaLocation", "location")
                .withPrefabValues(Pattern.class, Pattern.compile("red"), Pattern.compile("black"))
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void toStringTest() {
        JSONObject rawSchemaJson = ResourceLoader.DEFAULT.readObj("tostring/stringschema.json");
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toStringWithNullableTrueTest() {
        JSONObject rawSchemaJson = ResourceLoader.DEFAULT.readObj("tostring/stringschema.json");
        rawSchemaJson.put("nullable", true);
        String actual = loadWithNullableSupport(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toStringWithNullableFalseTest() {
        JSONObject rawSchemaJson = ResourceLoader.DEFAULT.readObj("tostring/stringschema.json");
        rawSchemaJson.put("nullable", false);
        String actual = loadWithNullableSupport(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toStringNoFormat() {
        JSONObject rawSchemaJson = ResourceLoader.DEFAULT.readObj("tostring/stringschema.json");
        rawSchemaJson.remove("format");
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toStringNoExplicitType() {
        JSONObject rawSchemaJson = ResourceLoader.DEFAULT.readObj("tostring/stringschema.json");
        rawSchemaJson.remove("type");
        String actual = SchemaLoader.load(rawSchemaJson).toString();
        assertThat(new JSONObject(actual), sameJsonAs(rawSchemaJson));
    }

    @Test
    public void toString_ReadOnlyWriteOnly() {
        org.everit.json.schema.Schema subject = org.everit.json.schema.StringSchema.builder().readOnly(true).writeOnly(false).build();
        JSONObject actual = new JSONObject(subject.toString());

        JSONObject expected = ResourceLoader.DEFAULT.readObj("tostring/stringschema-readonly-true-writeonly-false.json");

        assertThat(actual, sameJsonAs(expected));
    }

    @Test
    public void requiresString_nullable() {
        Schema subject = org.everit.json.schema.StringSchema.builder().requiresString(true).nullable(true).build();
        subject.validate(JSONObject.NULL);
    }

    @Test
    public void getConvertedPattern() {
        org.everit.json.schema.StringSchema subject = org.everit.json.schema.StringSchema.builder().pattern("my\\\\/[p]a[tt]ern").build();
        assertEquals("my\\\\/[p]a[tt]ern", subject.getRegexpPattern().toString());
        assertEquals("my\\\\/[p]a[tt]ern", subject.getPattern().toString());
    }

    @Test
    public void getConvertedNullPattern() {
        org.everit.json.schema.StringSchema subject = org.everit.json.schema.StringSchema.builder().build();
        assertNull(subject.getRegexpPattern());
        assertNull(subject.getPattern());
    }

    @Test
    public void regexpFactoryIsUsedByLoader() {
        SchemaLoader loader = SchemaLoader.builder()
                .regexpFactory(new RE2JRegexpFactory())
                .schemaJson(ResourceLoader.DEFAULT.readObj("tostring/stringschema.json"))
                .build();

        org.everit.json.schema.StringSchema result = (StringSchema) loader.load().build();

        assertEquals(result.getRegexpPattern().getClass().getSimpleName(), "RE2JRegexp");
    }

}
