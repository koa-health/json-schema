package com.koa.data.json.schema.regexp;

import com.koa.data.json.schema.regexp.Regexp;

public interface RegexpFactory {

    Regexp createHandler(String input);

}
