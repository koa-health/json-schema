package com.koa.data.json.schema;

import java.util.HashMap;
import java.util.Map;

import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.StringSchema;
import org.json.JSONObject;

public class DynamicReferenceSchemaExample {

    public static void main(String[] args) {
        org.everit.json.schema.ObjectSchema.Builder rootSchema = ObjectSchema.builder();
        org.everit.json.schema.ReferenceSchema referenceSchema = ReferenceSchema.builder()
            .refValue("#/definitions/MySubschema")
            .build();
        rootSchema.addPropertySchema("myProperty", referenceSchema);
        
        Map<String, Object> unprocessed = new HashMap<>();
        JSONObject defs = new JSONObject();
        org.everit.json.schema.StringSchema referredSchema = StringSchema.builder()
            .minLength(2).maxLength(5)
            .build();
        referenceSchema.setReferredSchema(referredSchema);
        defs.put("MySubschema", new JSONObject(referredSchema.toString()));
        unprocessed.put("definitions", defs);
        
        
        rootSchema.unprocessedProperties(unprocessed);

        System.out.println(rootSchema.build().toString());
    }
    
}
