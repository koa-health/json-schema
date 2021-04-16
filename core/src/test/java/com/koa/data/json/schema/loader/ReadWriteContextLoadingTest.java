package com.koa.data.json.schema.loader;

import com.koa.data.json.schema.ObjectSchema;
import com.koa.data.json.schema.ResourceLoader;
import com.koa.data.json.schema.Schema;
import org.junit.jupiter.api.Test;

import static com.koa.data.json.schema.TestSupport.loadAsV6;
import static com.koa.data.json.schema.TestSupport.loadAsV7;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadWriteContextLoadingTest {

    private static ResourceLoader LOADER = ResourceLoader.DEFAULT;

    @Test
    public void testReadOnlyContext() {
        ObjectSchema rootSchema = (ObjectSchema) loadAsV7(LOADER.readObj("read-write-context.json"));
        Schema readOnlyProp = rootSchema.getPropertySchemas().get("readOnlyProp");
        assertTrue(readOnlyProp.isReadOnly());
    }

    @Test
    public void testWriteOnlyContext() {
        ObjectSchema rootSchema = (ObjectSchema) loadAsV7(LOADER.readObj("read-write-context.json"));
        Schema writeOnlyProp = rootSchema.getPropertySchemas().get("writeOnlyProp");
        assertTrue(writeOnlyProp.isWriteOnly());
    }

    @Test
    public void worksOnlyInV7Mode() {
        ObjectSchema rootSchema = (ObjectSchema) loadAsV6(LOADER.readObj("read-write-context.json"));
        Schema readOnlyProp = rootSchema.getPropertySchemas().get("readOnlyProp");
        Schema writeOnlyProp = rootSchema.getPropertySchemas().get("writeOnlyProp");
        assertNull(readOnlyProp.isReadOnly());
        assertNull(writeOnlyProp.isWriteOnly());
    }

}
