package com.koa.data.json.schema.loader;

/**
 * @author erosb
 */
@FunctionalInterface
interface JsonObjectIterator {

    void apply(String key, JsonValue value);

}
