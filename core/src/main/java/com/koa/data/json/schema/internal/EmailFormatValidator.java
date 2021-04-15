package com.koa.data.json.schema.internal;

import java.util.Optional;

import org.apache.commons.validator.routines.EmailValidator;
import com.koa.data.json.schema.FormatValidator;

/**
 * Implementation of the "email" format value.
 */
public class EmailFormatValidator implements FormatValidator {

    @Override
    public Optional<String> validate(final String subject) {
        if (EmailValidator.getInstance(false, true).isValid(subject)) {
            return Optional.empty();
        }
        return Optional.of(String.format("[%s] is not a valid email address", subject));
    }

    @Override
    public String formatName() {
        return "email";
    }
}
