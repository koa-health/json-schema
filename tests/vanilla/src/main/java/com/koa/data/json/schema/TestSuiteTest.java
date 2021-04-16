package com.koa.data.json.schema;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestSuiteTest {

    private static JettyWrapper server;

    public static List<Arguments> params() {
        return TestCase.loadAsParamsFromPackage("com.koa.data.json.schema.draft4");
    }

    @BeforeAll
    public static void startJetty() throws Exception {
        (server = new JettyWrapper("/com/koa/data/json/schema/remotes")).start();
    }

    @AfterAll
    public static void stopJetty() throws Exception {
        server.stop();
    }

    @ParameterizedTest
    @MethodSource("params")
    public void testInCollectingMode(TestCase tc) {
        tc.loadSchema(SchemaLoader.builder());
        tc.runTestInCollectingMode();
    }

    @ParameterizedTest
    @MethodSource("params")
    public void testInEarlyFailingMode(TestCase tc) {
        tc.loadSchema(SchemaLoader.builder());
        tc.runTestInEarlyFailureMode();
    }

}
