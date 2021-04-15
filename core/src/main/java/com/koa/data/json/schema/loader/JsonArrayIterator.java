package com.koa.data.json.schema.loader;

import com.koa.data.json.schema.loader.JsonValue;

/**
 * @author erosb
 */
@FunctionalInterface
interface JsonArrayIterator {

    void apply(int index, JsonValue value);

}
