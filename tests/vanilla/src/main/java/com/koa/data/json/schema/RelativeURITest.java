package com.koa.data.json.schema;

import org.apache.commons.io.IOUtils;
import com.koa.data.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

public class RelativeURITest {

    @Test
    public void test() throws Exception {
        System.out.println(JettyWrapper.class
                .getResource("/com/koa/data/json/schema/relative-uri/").toExternalForm());

        JettyWrapper jetty = new JettyWrapper("/com/koa/data/json/schema/relative-uri");
        jetty.start();
        try {
            SchemaLoader.builder()
                    .resolutionScope("http://localhost:1234/schema/")
                    .schemaJson(
                            new JSONObject(new JSONTokener(IOUtils.toString(getClass().getResourceAsStream(
                                    "/com/koa/data/json/schema/relative-uri/schema/main.json")))))
                    .build().load().build();
        } finally {
            jetty.stop();
        }
    }

}
