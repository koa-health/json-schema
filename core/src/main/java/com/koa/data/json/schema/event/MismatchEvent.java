package com.koa.data.json.schema.event;

import com.koa.data.json.schema.ValidationException;

public interface MismatchEvent {

    ValidationException getFailure();

}
