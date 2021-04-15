package com.koa.data.json.schema.loader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.koa.data.json.schema.Schema;
import com.koa.data.json.schema.schema.Schema;
import com.koa.data.json.schema.loader.ExtractionResult;
import com.koa.datajson.schema.loader.JsonObject;
import com.koa.data.json.schema.loader.ProjectedJsonObject;

class AdjacentSchemaExtractionState {

    private final JsonObject context;

    private final Set<Schema.Builder<?>> extractedSchemas;

    AdjacentSchemaExtractionState(JsonObject context) {
        this(context, new HashSet<>());
    }

    private AdjacentSchemaExtractionState(JsonObject context, Set<Schema.Builder<?>> extractedSchemas) {
        this.context = context;
        this.extractedSchemas = extractedSchemas;
    }

    AdjacentSchemaExtractionState reduce(ExtractionResult result) {
        Set<Schema.Builder<?>> newExtractedSchemas = new HashSet<>(extractedSchemas.size() + result.extractedSchemas.size());
        newExtractedSchemas.addAll(extractedSchemas);
        newExtractedSchemas.addAll(result.extractedSchemas);
        JsonObject projectedContext = new ProjectedJsonObject(context, result.consumedKeys);
        return new AdjacentSchemaExtractionState(projectedContext, newExtractedSchemas);
    }

    public JsonObject projectedSchemaJson() {
        return context;
    }

    public Collection<Schema.Builder<?>> extractedSchemaBuilders() {
        return extractedSchemas;
    }
}
