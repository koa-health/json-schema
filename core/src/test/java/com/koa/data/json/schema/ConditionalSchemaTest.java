package com.koa.data.json.schema;

import static com.koa.data.json.schema.JSONMatcher.sameJsonAs;
import static org.hamcrest.MatcherAssert.assertThat;

import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.EmptySchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.StringSchema;
import org.everit.json.schema.TrueSchema;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class ConditionalSchemaTest {

    static final org.everit.json.schema.StringSchema MAX_LENGTH_STRING_SCHEMA = org.everit.json.schema.StringSchema.builder().maxLength(4).schemaLocation("#/else").build();

    static final org.everit.json.schema.StringSchema MIN_LENGTH_STRING_SCHEMA = org.everit.json.schema.StringSchema.builder().minLength(6).schemaLocation("#/then").build();

    static final org.everit.json.schema.StringSchema PATTERN_STRING_SCHEMA = org.everit.json.schema.StringSchema.builder().pattern("f.*o").schemaLocation("#/if").build();

    private static final ResourceLoader LOADER = new ResourceLoader("/org/everit/jsonvalidator/tostring/");

    private static org.everit.json.schema.ConditionalSchema.Builder initCompleteSchema() {
        return org.everit.json.schema.ConditionalSchema.builder()
                .ifSchema(TrueSchema.builder().build())
                .thenSchema(ObjectSchema.builder()
                        .requiresObject(true)
                        .addRequiredProperty("prop").build())
                .elseSchema(EmptySchema.builder().build());
    }

    // only if

    @Test
    public void onlyIfSuccessEvenIfDataIsInvalidAgainstSubschema() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(org.everit.json.schema.StringSchema.builder().maxLength(2).build()).build().validate("foo");
    }

    @Test
    public void onlyIfSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    // only then

    @Test
    public void onlyThenSuccessEvenIfDataIsInvalidAgainstSubschema() {
        org.everit.json.schema.ConditionalSchema.builder().thenSchema(org.everit.json.schema.StringSchema.builder().maxLength(2).build()).build().validate("foo");
    }

    @Test
    public void onlyThenSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().thenSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    // only else

    @Test
    public void onlyElseSuccessEvenIfDataIsInvalidAgainstSubschema() {
        org.everit.json.schema.ConditionalSchema.builder().elseSchema(StringSchema.builder().maxLength(1).build()).build().validate("foo");
    }

    @Test
    public void onlyElseSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    // if-then

    @Test
    public void ifSubschemaSuccessThenSubschemaFailure() {
        org.everit.json.schema.ConditionalSchema.Builder subject = org.everit.json.schema.ConditionalSchema.builder().ifSchema(MAX_LENGTH_STRING_SCHEMA)
                .thenSchema(PATTERN_STRING_SCHEMA);
        TestSupport.failureOf(subject)
                .expectedKeyword("then")
                .expectedPointer("#")
                .input("bar")
                .expect();
    }

    @Test
    public void ifSubschemaFailureThenSubschemaFailure() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("barbar");
    }

    @Test
    public void ifSubschemaSuccessThenSubschemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    @Test
    public void ifSubschemaFailureThenSubschemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("bar");
    }

    // if-else

    @Test
    public void ifSubschemaSuccessElseSubschemaFailure() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(MAX_LENGTH_STRING_SCHEMA).elseSchema(PATTERN_STRING_SCHEMA).build().validate("bar");
    }

    @Test
    public void ifSubschemaFailureElseSubschemaFailure() {
        org.everit.json.schema.ConditionalSchema.Builder subject = org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA)
                .elseSchema(MAX_LENGTH_STRING_SCHEMA);
        TestSupport.failureOf(subject)
                .expectedKeyword("else")
                .expectedPointer("#")
                .input("barbar")
                .expect();
    }

    @Test
    public void ifSubschemaSuccessElseSubschemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    @Test
    public void ifSubschemaFailureElseSubschemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("bar");
    }

    // then-else

    @Test
    public void thenSubschemaSuccessElseSubschemaFailure() {
        org.everit.json.schema.ConditionalSchema.builder().thenSchema(MAX_LENGTH_STRING_SCHEMA).elseSchema(PATTERN_STRING_SCHEMA).build().validate("bar");
    }

    @Test
    public void thenSubschemaFailureElseSubschemaFailure() {
        org.everit.json.schema.ConditionalSchema.builder().thenSchema(PATTERN_STRING_SCHEMA).elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("barbar");
    }

    @Test
    public void thenSubschemaSuccessElseSubschemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().thenSchema(PATTERN_STRING_SCHEMA).elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    @Test
    public void thenSubschemaFailureElseSubschemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().thenSchema(PATTERN_STRING_SCHEMA).elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("bar");
    }

    // if-then-else

    @Test
    public void ifSubschemaSuccessThenSubschemaSuccessElseSubSchemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA)
                .elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    @Test
    public void ifSubschemaSuccessThenSubschemaSuccessElseSubSchemaFailure() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA)
                .elseSchema(MIN_LENGTH_STRING_SCHEMA).build().validate("foo");
    }

    @Test
    public void ifSubschemaSuccessThenSubschemaFailureElseSubSchemaSuccess() {
        org.everit.json.schema.ConditionalSchema.Builder subject = org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA)
                .elseSchema(MIN_LENGTH_STRING_SCHEMA);
        TestSupport.failureOf(subject)
                .expectedKeyword("then")
                .expectedPointer("#")
                .input("foobar")
                .expect();
    }

    @Test
    public void ifSubschemaSuccessThenSubschemaFailureElseSubSchemaFailure() {
        org.everit.json.schema.ConditionalSchema.Builder subject = org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA)
                .elseSchema(MIN_LENGTH_STRING_SCHEMA);
        TestSupport.failureOf(subject)
                .expectedKeyword("then")
                .expectedPointer("#")
                .input("foooo")
                .expect();
    }

    @Test
    public void ifSubschemaFailureThenSubschemaSuccessElseSubSchemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(MAX_LENGTH_STRING_SCHEMA).thenSchema(PATTERN_STRING_SCHEMA)
                .elseSchema(MIN_LENGTH_STRING_SCHEMA).build().validate("foobar");
    }

    @Test
    public void ifSubschemaFailureThenSubschemaSuccessElseSubSchemaFailure() {
        org.everit.json.schema.ConditionalSchema.Builder subject = org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MAX_LENGTH_STRING_SCHEMA)
                .elseSchema(MIN_LENGTH_STRING_SCHEMA);
        TestSupport.failureOf(subject)
                .expectedKeyword("else")
                .expectedPointer("#")
                .input("bar")
                .expect();
    }

    @Test
    public void ifSubschemaFailureThenSubschemaFailureElseSubSchemaSuccess() {
        org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MIN_LENGTH_STRING_SCHEMA)
                .elseSchema(MAX_LENGTH_STRING_SCHEMA).build().validate("bar");
    }

    @Test
    public void ifSubschemaFailureThenSubschemaFailureElseSubSchemaFailure() {
        org.everit.json.schema.ConditionalSchema.Builder subject = org.everit.json.schema.ConditionalSchema.builder().ifSchema(PATTERN_STRING_SCHEMA).thenSchema(MIN_LENGTH_STRING_SCHEMA)
                .elseSchema(MAX_LENGTH_STRING_SCHEMA);
        TestSupport.failureOf(subject)
                .expectedKeyword("else")
                .expectedPointer("#")
                .input("barbarbar")
                .expect();
    }

    @Test
    public void toStringTest() {
        org.everit.json.schema.ConditionalSchema subject = initCompleteSchema().build();

        JSONObject actual = new JSONObject(subject.toString());

        assertThat(actual, sameJsonAs(LOADER.readObj("conditionalschema.json")));
    }

    @Test
    public void toString_noIf() {
        org.everit.json.schema.ConditionalSchema subject = initCompleteSchema().ifSchema(null).build();
        JSONObject expectedSchemaJson = LOADER.readObj("conditionalschema.json");
        expectedSchemaJson.remove("if");

        JSONObject actual = new JSONObject(subject.toString());

        assertThat(actual, sameJsonAs(expectedSchemaJson));
    }

    @Test
    public void toString_noThen() {
        org.everit.json.schema.ConditionalSchema subject = initCompleteSchema().thenSchema(null).build();
        JSONObject expectedSchemaJson = LOADER.readObj("conditionalschema.json");
        expectedSchemaJson.remove("then");

        JSONObject actual = new JSONObject(subject.toString());

        assertThat(actual, sameJsonAs(expectedSchemaJson));
    }

    @Test
    public void toString_noElse() {
        ConditionalSchema subject = initCompleteSchema().thenSchema(null).elseSchema(null).build();
        JSONObject expectedSchemaJson = LOADER.readObj("conditionalschema.json");
        expectedSchemaJson.remove("then");
        expectedSchemaJson.remove("else");

        JSONObject actual = new JSONObject(subject.toString());

        assertThat(actual, sameJsonAs(expectedSchemaJson));
    }

}
