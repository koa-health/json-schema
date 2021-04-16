package com.koa.data.json.schema.loader;

/**
 * @author erosb
 */
@FunctionalInterface
interface JsonArrayIterator {

    void apply(int index, JsonValue value);

}
