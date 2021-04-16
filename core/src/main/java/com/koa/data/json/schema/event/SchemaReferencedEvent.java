package com.koa.data.json.schema.event;

import java.util.List;
import java.util.Objects;

import com.koa.data.json.schema.ReferenceSchema;
import com.koa.data.json.schema.Schema;
import org.json.JSONObject;

public class SchemaReferencedEvent extends ValidationEvent<ReferenceSchema> {

    private final Schema referredSchema;

    public SchemaReferencedEvent(ReferenceSchema schema, Object instance, Schema referredSchema) {
        super(schema, instance);
        this.referredSchema = referredSchema;
    }
    public SchemaReferencedEvent(ReferenceSchema schema, Object instance, Schema referredSchema,
                                 List<String> path) {
        super(schema, instance, path);
        this.referredSchema = referredSchema;
    }


    @Override
    void describeTo(JSONObject obj) {
        obj.put("type", "ref");
    }

    public Schema getReferredSchema() {
        return referredSchema;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!canEqual(o)) {
            return false;
        }
        if (!super.equals(o))
            return false;
        SchemaReferencedEvent that = (SchemaReferencedEvent) o;
        return referredSchema.equals(that.referredSchema);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), referredSchema);
    }

    @Override boolean canEqual(Object o) {
        return o instanceof SchemaReferencedEvent;
    }
}
