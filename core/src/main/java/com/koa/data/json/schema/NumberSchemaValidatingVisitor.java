package com.koa.data.json.schema;

import java.math.BigDecimal;
import java.util.List;

import static com.koa.data.json.schema.NumberComparator.compare;
import static com.koa.data.json.schema.NumberComparator.getAsBigDecimal;


class NumberSchemaValidatingVisitor extends Visitor {

    private final Object subject;

    private final ValidatingVisitor owner;

    private boolean exclusiveMinimum;

    private boolean exclusiveMaximum;

    private Number numberSubject;

    NumberSchemaValidatingVisitor(Object subject, ValidatingVisitor owner) {
        this.subject = subject;
        this.owner = owner;
    }

    @Override
    void visitNumberSchema(NumberSchema numberSchema, List<String> path) {
        Class expectedType = numberSchema.requiresInteger() ? Integer.class : Number.class;
        if (owner.passesTypeCheck(expectedType, numberSchema.requiresInteger() || numberSchema.isRequiresNumber(), numberSchema.isNullable())) {
            this.numberSubject = ((Number) subject);
            super.visitNumberSchema(numberSchema, path);
        }
    }

    @Override
    void visitExclusiveMinimum(boolean exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    @Override
    void visitMinimum(Number minimum) {
        if (minimum == null) {
            return;
        }
        int comparison = compare(numberSubject, minimum);
        if (exclusiveMinimum && comparison <= 0) {
            owner.failure(subject + " is not greater than " + minimum, "exclusiveMinimum");
        } else if (comparison < 0) {
            owner.failure(subject + " is not greater or equal to " + minimum, "minimum");
        }
    }

    @Override
    void visitExclusiveMinimumLimit(Number exclusiveMinimumLimit) {
        if (exclusiveMinimumLimit != null) {
            if (compare(numberSubject, exclusiveMinimumLimit) <= 0) {
                owner.failure(subject + " is not greater than " + exclusiveMinimumLimit, "exclusiveMinimum");
            }
        }
    }

    @Override
    void visitMaximum(Number maximum) {
        if (maximum == null) {
            return;
        }
        int comparison = compare(maximum, numberSubject);
        if (exclusiveMaximum && comparison <= 0) {
            owner.failure(subject + " is not less than " + maximum, "exclusiveMaximum");
        } else if (comparison < 0) {
            owner.failure(subject + " is not less or equal to " + maximum, "maximum");
        }
    }

    @Override
    void visitExclusiveMaximum(boolean exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    @Override
    void visitExclusiveMaximumLimit(Number exclusiveMaximumLimit) {
        if (exclusiveMaximumLimit != null) {
            if (compare(numberSubject, exclusiveMaximumLimit) >= 0) {
                owner.failure(subject + " is not less than " + exclusiveMaximumLimit, "exclusiveMaximum");
            }
        }
    }

    @Override
    void visitMultipleOf(Number multipleOf) {
        if (multipleOf != null) {
            BigDecimal remainder = getAsBigDecimal(numberSubject).remainder(
                getAsBigDecimal(multipleOf));
            if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                owner.failure(subject + " is not a multiple of " + multipleOf, "multipleOf");
            }
        }
    }
}
