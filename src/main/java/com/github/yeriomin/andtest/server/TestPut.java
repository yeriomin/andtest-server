package com.github.yeriomin.andtest.server;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.InputStream;

public class TestPut extends RequestProcessor {

    public TestPut(Request request, Response response) {
        super(request, response);
    }

    public JSONObject getResponseObject() {
        JSONObject rawSchema;
        try (InputStream inputStream = getClass().getResourceAsStream("/test.schema.json")) {
            rawSchema = new JSONObject(new JSONTokener(inputStream));
        } catch (IOException e) {
            rawSchema = new JSONObject();
            // Could not find schema file - should not happen
        }
        Schema schema = SchemaLoader.load(rawSchema);
        JSONObject testJson = new JSONObject(this.request.body());
        schema.validate(testJson);
        String id = new DbHandler().put(testJson);
        return JSend.success(new JSONObject().put("id", id));
    }
}
