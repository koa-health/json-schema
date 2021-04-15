package com.koa.data.json.schema.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koa.data.json.schema.CombinedSchema;
import com.koa.data.json.schema.Schema;

public abstract class CombinedSchemaValidationEvent extends ValidationEvent<CombinedSchema> {

    final Schema subSchema;

    public CombinedSchemaValidationEvent(CombinedSchema schema, Schema subSchema, Object instance) {
        super(schema, instance);
        this.subSchema = subSchema;
    }
    public CombinedSchemaValidationEvent(CombinedSchema schema, Schema subSchema, Object instance, List<String> path) {
        super(schema, instance, path);
        this.subSchema = subSchema;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CombinedSchemaValidationEvent))
            return false;
        if (!super.equals(o))
            return false;
        CombinedSchemaValidationEvent that = (CombinedSchemaValidationEvent) o;
        return subSchema.equals(that.subSchema);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), subSchema);
    }

    public Schema getSubSchema() {
        return subSchema;
    }
}
