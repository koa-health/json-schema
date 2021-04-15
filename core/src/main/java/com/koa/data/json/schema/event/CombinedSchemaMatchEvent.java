package com.koa.data.json.schema.event;

import com.koa.data.json.schema.CombinedSchema;
import com.koa.data.json.schema.Schema;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CombinedSchemaMatchEvent extends CombinedSchemaValidationEvent {

    public CombinedSchemaMatchEvent(CombinedSchema schema, Schema subSchema,
                                    Object instance) {
        super(schema, subSchema, instance);
    }
    public CombinedSchemaMatchEvent(CombinedSchema schema, Schema subSchema,
                                    Object instance, List<String> path) {
        super(schema, subSchema, instance, path);
    }

    @Override void describeTo(JSONObject obj) {
        obj.put("type", "match");
        obj.put("keyword", schema.getCriterion().toString());
    }

    @Override public boolean equals(Object o) {
        return o instanceof CombinedSchemaMatchEvent && super.equals(o);
    }

    @Override boolean canEqual(Object o) {
        return o instanceof CombinedSchemaMatchEvent;
    }
}
