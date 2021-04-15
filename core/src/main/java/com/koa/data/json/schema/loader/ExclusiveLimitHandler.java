package com.koa.data.json.schema.loader;

import com.koa.data.json.schema.NumberSchema;
import com.koa.data.json.schema.schema.NumberSchema;
import com.koa.data.json.schema.loader.JsonValue;
import com.koa.data.json.schema.loader.SpecificationVersion;

class V4ExclusiveLimitHandler implements  ExclusiveLimitHandler {

    @Override
    public void handleExclusiveMinimum(JsonValue exclMinimum, NumberSchema.Builder schemaBuilder) {
        schemaBuilder.exclusiveMinimum(exclMinimum.requireBoolean());
    }

    @Override
    public void handleExclusiveMaximum(JsonValue exclMaximum, NumberSchema.Builder schemaBuilder) {
        schemaBuilder.exclusiveMaximum(exclMaximum.requireBoolean());
    }
}

class V6ExclusiveLimitHandler implements ExclusiveLimitHandler {

    @Override
    public void handleExclusiveMinimum(JsonValue exclMinimum, NumberSchema.Builder schemaBuilder) {
        schemaBuilder.exclusiveMinimum(exclMinimum.requireNumber());
    }

    @Override
    public void handleExclusiveMaximum(JsonValue exclMaximum, NumberSchema.Builder schemaBuilder) {
        schemaBuilder.exclusiveMaximum(exclMaximum.requireNumber());
    }

}

interface ExclusiveLimitHandler {

    static ExclusiveLimitHandler ofSpecVersion(SpecificationVersion specVersion) {
        switch (specVersion) {
            case DRAFT_4: return new V4ExclusiveLimitHandler();
            case DRAFT_6:
            case DRAFT_7: return new V6ExclusiveLimitHandler();
            default: throw new RuntimeException("unknown spec version: " + specVersion);
        }
    }

    void handleExclusiveMinimum(JsonValue exclMinimum, NumberSchema.Builder schemaBuilder);

    void handleExclusiveMaximum(JsonValue exclMaximum, NumberSchema.Builder schemaBuilder);

}
