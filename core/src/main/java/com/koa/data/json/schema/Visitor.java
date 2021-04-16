package com.koa.data.json.schema;

import com.koa.data.json.schema.regexp.Regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class Visitor {

    List<String> appendPath(List<String> path, int index) {
        return appendPath(path, String.format("[%d]", index));
    }

    List<String> appendPath(List<String> path, String field) {
        List<String> newList = new ArrayList<>(path);
        newList.add(field);
        return newList;
    }

    //void visitSchema(Schema schema) {}
    void visitSchema(Schema schema, List<String> path) {
        //schema.accept(this, path);
    }


    void visitNumberSchema(NumberSchema numberSchema, List<String> path) {
        visitSchema(numberSchema, path);
        visitExclusiveMinimum(numberSchema.isExclusiveMinimum());
        visitMinimum(numberSchema.getMinimum());
        visitExclusiveMinimumLimit(numberSchema.getExclusiveMinimumLimit());
        visitExclusiveMaximum(numberSchema.isExclusiveMaximum());
        visitMaximum(numberSchema.getMaximum());
        visitExclusiveMaximumLimit(numberSchema.getExclusiveMaximumLimit());
        visitMultipleOf(numberSchema.getMultipleOf());
    }

    void visitMinimum(Number minimum) {
    }

    void visitExclusiveMinimum(boolean exclusiveMinimum) {
    }

    void visitExclusiveMinimumLimit(Number exclusiveMinimumLimit) {
    }

    void visitMaximum(Number maximum) {
    }

    void visitExclusiveMaximum(boolean exclusiveMaximum) {
    }

    void visitExclusiveMaximumLimit(Number exclusiveMaximumLimit) {
    }

    void visitMultipleOf(Number multipleOf) {
    }

    void visit(Schema schema) {
        schema.accept(this);
    }

    void visit(Schema schema, List<String> path) {
        schema.accept(this, path);
    }

    void visitArraySchema(ArraySchema arraySchema, List<String> path) {
        visitSchema(arraySchema, path);
        visitMinItems(arraySchema.getMinItems());
        visitMaxItems(arraySchema.getMaxItems());
        visitUniqueItems(arraySchema.needsUniqueItems());
        if (arraySchema.getAllItemSchema() != null) {
            visitAllItemSchema(arraySchema.getAllItemSchema(), path);
        }
        visitAdditionalItems(arraySchema.permitsAdditionalItems());
        if (arraySchema.getItemSchemas() != null) {
            visitItemSchemas(arraySchema.getItemSchemas(), path);
        }
        if (arraySchema.getSchemaOfAdditionalItems() != null) {
            visitSchemaOfAdditionalItems(arraySchema.getSchemaOfAdditionalItems(), path);
        }
        if (arraySchema.getContainedItemSchema() != null) {
            visitContainedItemSchema(arraySchema.getContainedItemSchema(), path);
        }
    }

    void visitItemSchemas(List<Schema> itemSchemas, List<String> path) {
        if (itemSchemas != null) {
            for (int i = 0; i < itemSchemas.size(); ++i) {
                visitItemSchema(i, itemSchemas.get(i), path);
            }
        }
    }

    void visitMinItems(Integer minItems) {
    }

    void visitMaxItems(Integer maxItems) {
    }

    void visitUniqueItems(boolean uniqueItems) {
    }

    void visitAllItemSchema(Schema allItemSchema, List<String> path) {
        visitSchema(allItemSchema, path);
    }

    void visitAdditionalItems(boolean additionalItems) {
    }

    void visitItemSchema(int index, Schema itemSchema, List<String> path) {
        visitSchema(itemSchema, path);
    }

    void visitSchemaOfAdditionalItems(Schema schemaOfAdditionalItems, List<String> path) {
        visitSchema(schemaOfAdditionalItems, path);
    }

    void visitContainedItemSchema(Schema containedItemSchema, List<String> path) {
        visitSchema(containedItemSchema, path);
    }

    void visitBooleanSchema(BooleanSchema schema, List<String> path) {
        visitSchema(schema, path);
    }

    void visitNullSchema(NullSchema nullSchema, List<String> path) {
        visitSchema(nullSchema, path);
    }

    void visitEmptySchema(EmptySchema emptySchema, List<String> path) {
        visitSchema(emptySchema, path);
    }

    void visitConstSchema(ConstSchema constSchema, List<String> path) {
        visitSchema(constSchema, path);
    }

    void visitEnumSchema(EnumSchema enumSchema, List<String> path) {
        visitSchema(enumSchema, path);
    }

    void visitFalseSchema(FalseSchema falseSchema, List<String> path) {
        visitSchema(falseSchema, path);
    }

    void visitNotSchema(NotSchema notSchema, List<String> path) {
        visitSchema(notSchema, path);
        notSchema.getMustNotMatch().accept(this);
    }

    void visitReferenceSchema(ReferenceSchema referenceSchema, List<String> path) {
        visitSchema(referenceSchema, path);
    }

    void visitObjectSchema(ObjectSchema objectSchema, List<String> path) {
        visitSchema(objectSchema, path);
        visitRequiredProperties(objectSchema.getRequiredProperties(), path);
        if (objectSchema.getPropertyNameSchema() != null) {
            visitPropertyNameSchema(objectSchema.getPropertyNameSchema(), path);
        }
        visitMinProperties(objectSchema.getMinProperties());
        visitMaxProperties(objectSchema.getMaxProperties());
        for (Map.Entry<String, Set<String>> entry : objectSchema.getPropertyDependencies().entrySet()) {
            visitPropertyDependencies(entry.getKey(), entry.getValue());
        }
        visitAdditionalProperties(objectSchema.permitsAdditionalProperties());
        if (objectSchema.getSchemaOfAdditionalProperties() != null) {
            visitSchemaOfAdditionalProperties(objectSchema.getSchemaOfAdditionalProperties(), path);
        }
        Map<Regexp, Schema> patternProperties = objectSchema.getRegexpPatternProperties();
        if (patternProperties != null) {
            visitPatternProperties(patternProperties, path);
        }
        for (Map.Entry<String, Schema> schemaDep : objectSchema.getSchemaDependencies().entrySet()) {
            visitSchemaDependency(schemaDep.getKey(), schemaDep.getValue(), path);
        }
        Map<String, Schema> propertySchemas = objectSchema.getPropertySchemas();
        if (propertySchemas != null) {
            visitPropertySchemas(propertySchemas, path);
        }
    }

    void visitRequiredProperties(List<String> requiredProperties, List<String> path) {
        for (String requiredPropName : requiredProperties) {
            visitRequiredPropertyName(requiredPropName, path);
        }
    }

    void visitPatternProperties(Map<Regexp, Schema> patternProperties, List<String> path) {
        for (Map.Entry<Regexp, Schema> entry : patternProperties.entrySet()) {
            visitPatternPropertySchema(entry.getKey(), entry.getValue(), path);
        }
    }

    void visitPropertySchemas(Map<String, Schema> propertySchemas, List<String> path) {
        for (Map.Entry<String, Schema> entry : propertySchemas.entrySet()) {
            visitPropertySchema(entry.getKey(), entry.getValue(), path);
        }
    }

    void visitPropertySchema(String properyName, Schema schema, List<String> path) {
        visitSchema(schema, path);
    }

    void visitSchemaDependency(String propKey, Schema schema, List<String> path) {
        visitSchema(schema, path);
    }

    void visitPatternPropertySchema(Regexp propertyNamePattern, Schema schema, List<String> path) {
        visitSchema(schema, path);
    }

    void visitSchemaOfAdditionalProperties(Schema schemaOfAdditionalProperties, List<String> path) {
        visitSchema(schemaOfAdditionalProperties, path);
    }

    void visitAdditionalProperties(boolean additionalProperties) {
    }

    void visitPropertyDependencies(String ifPresent, Set<String> allMustBePresent) {
    }

    void visitMaxProperties(Integer maxProperties) {
    }

    void visitMinProperties(Integer minProperties) {
    }

    void visitPropertyNameSchema(Schema propertyNameSchema, List<String> path) {
        visitSchema(propertyNameSchema, path);
    }

    void visitRequiredPropertyName(String requiredPropName, List<String> path) {
    }

    void visitStringSchema(StringSchema stringSchema, List<String> path) {
        visitSchema(stringSchema, path);
        visitMinLength(stringSchema.getMinLength());
        visitMaxLength(stringSchema.getMaxLength());
        visitPattern(stringSchema.getRegexpPattern());
        visitFormat(stringSchema.getFormatValidator());
    }

    void visitFormat(FormatValidator formatValidator) {
    }

    void visitPattern(Regexp pattern) {
    }

    void visitMaxLength(Integer maxLength) {
    }

    void visitMinLength(Integer minLength) {
    }

    void visitCombinedSchema(CombinedSchema combinedSchema, List<String> path) {
        visitSchema(combinedSchema, path);
    }

    void visitConditionalSchema(ConditionalSchema conditionalSchema, List<String> path) {
        visitSchema(conditionalSchema, path);
        conditionalSchema.getIfSchema().ifPresent(schema -> visitIfSchema(schema, path));
        conditionalSchema.getThenSchema().ifPresent(schema -> visitThenSchema(schema, path));
        conditionalSchema.getElseSchema().ifPresent(schema -> visitElseSchema(schema, path));
    }

    void visitIfSchema(Schema ifSchema, List<String> path) {
        visitSchema(ifSchema, path);
    }

    void visitThenSchema(Schema thenSchema, List<String> path) {
        visitSchema(thenSchema, path);
    }

    void visitElseSchema(Schema elseSchema, List<String> path) {
        visitSchema(elseSchema, path);
    }
}
