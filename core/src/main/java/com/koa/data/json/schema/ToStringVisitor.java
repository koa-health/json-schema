package com.koa.data.json.schema;

import static com.koa.data.json.schema.FormatValidator.NONE;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.koa.data.json.schema.internal.JSONPrinter;
import com.koa.data.json.schema.loader.SpecificationVersion;
import com.koa.data.json.schema.regexp.Regexp;
import org.json.JSONException;

class ToStringVisitor extends Visitor {

    private final JSONPrinter writer;

    private boolean jsonObjectIsOpenForCurrentSchemaInstance = false;

    private boolean skipNextObject = false;

    private SpecificationVersion deducedSpecVersion;

    ToStringVisitor(JSONPrinter writer) {
        this.writer = writer;
    }

    @Override void visitSchema(Schema schema, List<String> path) {
        if (schema == null) {
            return;
        }
        if (!jsonObjectIsOpenForCurrentSchemaInstance) {
            writer.object();
        }
        writer.ifPresent("title", schema.getTitle());
        writer.ifPresent("description", schema.getDescription());
        writer.ifPresent("nullable", schema.isNullable());
        writer.ifPresent("default", schema.getDefaultValue());
        writer.ifPresent("readOnly", schema.isReadOnly());
        writer.ifPresent("writeOnly", schema.isWriteOnly());
        super.visitSchema(schema, path);
        Object schemaKeywordValue = schema.getUnprocessedProperties().get("$schema");
        String idKeyword = deduceSpecVersion(schemaKeywordValue).idKeyword();
        writer.ifPresent(idKeyword, schema.getId());
        schema.getUnprocessedProperties().forEach((key, val) -> writer.key(key).value(val));
        schema.describePropertiesTo(writer);
        if (!jsonObjectIsOpenForCurrentSchemaInstance) {
            writer.endObject();
        }
    }

    private SpecificationVersion deduceSpecVersion(Object schemaKeywordValue) {
        if (deducedSpecVersion != null) {
            return deducedSpecVersion;
        }
        if (schemaKeywordValue instanceof String) {
            return deducedSpecVersion = SpecificationVersion.lookupByMetaSchemaUrl((String) schemaKeywordValue)
                    .orElse(SpecificationVersion.DRAFT_4);
        } else {
            return deducedSpecVersion = SpecificationVersion.DRAFT_4;
        }
    }

    private void printInJsonObject(Runnable task) {
        if (skipNextObject) {
            skipNextObject = false;
            jsonObjectIsOpenForCurrentSchemaInstance = true;
            task.run();
            jsonObjectIsOpenForCurrentSchemaInstance = false;
        } else {
            writer.object();
            jsonObjectIsOpenForCurrentSchemaInstance = true;
            task.run();
            writer.endObject();
            jsonObjectIsOpenForCurrentSchemaInstance = false;
        }
    }

    @Override
    void visitBooleanSchema(BooleanSchema schema, List<String> path) {
        printInJsonObject(() -> {
            super.visitBooleanSchema(schema, path);
            writer.key("type").value("boolean");
        });
    }

    @Override void visitArraySchema(ArraySchema schema, List<String> path) {
        printInJsonObject(() -> {
            if (schema.requiresArray()) {
                writer.key("type").value("array");
            }
            writer.ifTrue("uniqueItems", schema.needsUniqueItems())
                    .ifPresent("minItems", schema.getMinItems())
                    .ifPresent("maxItems", schema.getMaxItems())
                    .ifFalse("additionalItems", schema.permitsAdditionalItems());
            super.visitArraySchema(schema, path);
        });
    }

    @Override void visit(Schema schema, List<String> path) {
        boolean orig = jsonObjectIsOpenForCurrentSchemaInstance;
        jsonObjectIsOpenForCurrentSchemaInstance = false;
        super.visit(schema, path);
        jsonObjectIsOpenForCurrentSchemaInstance = orig;
    }

    @Override void visitAllItemSchema(Schema allItemSchema, List<String> path) {
        writer.key("items");
        visit(allItemSchema, path);
    }

    @Override void visitEmptySchema(EmptySchema emptySchema, List<String> path) {
        if (emptySchema instanceof TrueSchema) {
            writer.value(true);
        } else {
            printInJsonObject(() -> super.visitEmptySchema(emptySchema, path));
        }
    }

    @Override void visitItemSchemas(List<Schema> itemSchemas, List<String> path) {
        writer.key("items");
        writer.array();
        super.visitItemSchemas(itemSchemas, path);
        writer.endArray();
    }

    @Override void visitItemSchema(int index, Schema itemSchema, List<String> path) {
        visit(itemSchema, path);
    }

    @Override void visitSchemaOfAdditionalItems(Schema schemaOfAdditionalItems, List<String> path) {
        writer.key("additionalItems");
        visit(schemaOfAdditionalItems, path);
    }

    @Override void visitContainedItemSchema(Schema containedItemSchema, List<String> path) {
        writer.key("contains");
        visit(containedItemSchema, path);
    }

    @Override void visitConditionalSchema(ConditionalSchema conditionalSchema, List<String> path) {
        printInJsonObject(() -> super.visitConditionalSchema(conditionalSchema, path));
    }

    @Override void visitNotSchema(NotSchema notSchema, List<String> path) {
        printInJsonObject(() -> {
            visitSchema(notSchema, path);
            writer.key("not");
            notSchema.getMustNotMatch().accept(this);
        });
    }

    @Override void visitNumberSchema(NumberSchema schema, List<String> path) {
        printInJsonObject(() -> {
            if (schema.requiresInteger()) {
                writer.key("type").value("integer");
            } else if (schema.isRequiresNumber()) {
                writer.key("type").value("number");
            }
            writer.ifPresent("minimum", schema.getMinimum());
            writer.ifPresent("maximum", schema.getMaximum());
            writer.ifPresent("multipleOf", schema.getMultipleOf());
            writer.ifTrue("exclusiveMinimum", schema.isExclusiveMinimum());
            writer.ifTrue("exclusiveMaximum", schema.isExclusiveMaximum());
            try {
                writer.ifPresent("exclusiveMinimum", schema.getExclusiveMinimumLimit());
                writer.ifPresent("exclusiveMaximum", schema.getExclusiveMaximumLimit());
            } catch (JSONException e) {
                throw new IllegalStateException("overloaded use of exclusiveMinimum or exclusiveMaximum keyword");
            }
            super.visitNumberSchema(schema, path);
        });
    }

    @Override void visitConstSchema(ConstSchema constSchema, List<String> path) {
        printInJsonObject(() -> {
            writer.key("const");
            writer.value(constSchema.getPermittedValue());
            super.visitConstSchema(constSchema, path);
        });
    }

    @Override void visitObjectSchema(ObjectSchema schema, List<String> path) {
        printInJsonObject(() -> {
            if (schema.requiresObject()) {
                writer.key("type").value("object");
            }
            writer.ifPresent("minProperties", schema.getMinProperties());
            writer.ifPresent("maxProperties", schema.getMaxProperties());
            if (!schema.getPropertyDependencies().isEmpty()) {
                describePropertyDependencies(schema.getPropertyDependencies());
            }
            if (!schema.getSchemaDependencies().isEmpty()) {
                writer.key("dependencies");
                printSchemaMap(schema.getSchemaDependencies(), path);
            }
            writer.ifFalse("additionalProperties", schema.permitsAdditionalProperties());
            super.visitObjectSchema(schema, path);
        });
    }

    @Override void visitRequiredProperties(List<String> requiredProperties, List<String> path) {
        if (!requiredProperties.isEmpty()) {
            writer.key("required").value(requiredProperties);
        }
    }

    @Override void visitSchemaOfAdditionalProperties(Schema schemaOfAdditionalProperties, List<String> path) {
        writer.key("additionalProperties");
        visit(schemaOfAdditionalProperties, path);
    }

    private void describePropertyDependencies(Map<String, Set<String>> propertyDependencies) {
        writer.key("dependencies");
        writer.object();
        propertyDependencies.forEach((key, value) -> {
            writer.key(key);
            writer.array();
            value.forEach(writer::value);
            writer.endArray();
        });
        writer.endObject();
    }

    @Override void visitPropertyNameSchema(Schema propertyNameSchema, List<String> path) {
        writer.key("propertyNames");
        visit(propertyNameSchema, path);
    }

    @Override void visitPropertySchemas(Map<String, Schema> propertySchemas, List<String> path) {
        if (!propertySchemas.isEmpty()) {
            writer.key("properties");
            printSchemaMap(propertySchemas, path);
        }
    }

    private void printSchemaMap(Map<?, Schema> schemas, List<String> path) {
        writer.object();
        schemas.forEach((key, value) -> {
            writer.key(key.toString());
            visit(value, path);
        });
        writer.endObject();
    }

    @Override void visitPatternProperties(Map<Regexp, Schema> patternProperties, List<String> path) {
        if (!patternProperties.isEmpty()) {
            writer.key("patternProperties");
            printSchemaMap(patternProperties, path);
        }
    }

    @Override void visitCombinedSchema(CombinedSchema combinedSchema, List<String> path) {
        printInJsonObject(() -> {
            super.visitCombinedSchema(combinedSchema, path);
            if (combinedSchema.isSynthetic()) {
                combinedSchema.getSubschemas().forEach(subschema -> {
                    this.skipNextObject = true;
                    super.visit(subschema, path);
                });
            } else {
                writer.key(combinedSchema.getCriterion().toString());
                writer.array();
                combinedSchema.getSubschemas().forEach(subschema -> subschema.accept(this, path));
                writer.endArray();
            }
        });

    }

    @Override void visitIfSchema(Schema ifSchema, List<String> path) {
        writer.key("if");
        visit(ifSchema, path);
    }

    @Override void visitThenSchema(Schema thenSchema, List<String> path) {
        writer.key("then");
        visit(thenSchema, path);
    }

    @Override void visitElseSchema(Schema elseSchema, List<String> path) {
        writer.key("else");
        visit(elseSchema, path);
    }

    @Override void visitFalseSchema(FalseSchema falseSchema, List<String> path) {
        writer.value(false);
    }

    @Override void visitNullSchema(NullSchema nullSchema, List<String> path) {
        printInJsonObject(() -> {
            writer.key("type");
            writer.value("null");
            super.visitNullSchema(nullSchema, path);
        });
    }

    @Override void visitStringSchema(StringSchema schema, List<String> path) {
        printInJsonObject(() -> {
            if (schema.requireString()) {
                writer.key("type").value("string");
            }
            writer.ifPresent("minLength", schema.getMinLength());
            writer.ifPresent("maxLength", schema.getMaxLength());
            writer.ifPresent("pattern", schema.getPattern());
            if (schema.getFormatValidator() != null && !NONE.equals(schema.getFormatValidator())) {
                writer.key("format").value(schema.getFormatValidator().formatName());
            }
            super.visitStringSchema(schema, path);
        });
    }

    @Override void visitEnumSchema(EnumSchema schema, List<String> path) {
        printInJsonObject(() -> {
            writer.key("enum");
            writer.array();
            schema.getPossibleValues().forEach(writer::value);
            writer.endArray();
            super.visitEnumSchema(schema, path);
        });
    }

    @Override void visitReferenceSchema(ReferenceSchema referenceSchema, List<String> path) {
        printInJsonObject(() -> {
            writer.key("$ref");
            writer.value(referenceSchema.getReferenceValue());
            super.visitReferenceSchema(referenceSchema, path);
        });
    }
}
