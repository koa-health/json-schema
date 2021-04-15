package com.koa.data.json.schema.loader;

import com.koa.data.json.schema.loader.JsonValue;

/**
 * @author erosb
 */
@FunctionalInterface
interface JsonObjectIterator {

    void apply(String key, JsonValue value);

}
