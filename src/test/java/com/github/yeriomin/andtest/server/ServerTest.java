package com.github.yeriomin.andtest.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import okhttp3.*;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import spark.Spark;

import java.util.Properties;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServerTest extends TestCase {

    static private String id;

    public ServerTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return new TestSuite(ServerTest.class);
    }

    @org.junit.Before
    public void setUp() throws Exception {
        Server.main(new String[]{});
        Properties properties = DbHandler.getConfig();
        properties.setProperty("mongo.dbname", "andtesttest");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        Spark.stop();
    }

    @org.junit.Test
    public void test1GetEmpty() throws Exception {
        DbHandler.getCollection().drop();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://localhost:4567/api/v1/test").build();
        Response response = client.newCall(request).execute();
        assertEquals("{\"data\":[],\"status\":\"success\"}", response.body().string());
    }

    @org.junit.Test
    public void test2Put() throws Exception {
        OkHttpClient client = new OkHttpClient();

        String testJson = "{\"timeLimit\":10,\"description\":\"d\",\"questions\":[{\"question\":\"q\",\"type\":\"openEnded\",\"correct\":\"c\",\"explanation\":\"e\"}]}";
        MediaType type = MediaType.parse("application/json");
        Request request = new Request
                .Builder()
                .url("http://localhost:4567/api/v1/test")
                .put(RequestBody.create(type, testJson))
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject responseJSON = new JSONObject(responseBody);
        assertTrue(responseJSON.has("status"));
        assertEquals("success", responseJSON.getString("status"));
        assertTrue(responseJSON.has("data"));
        id = responseJSON.getJSONObject("data").getString("id");
    }

    @org.junit.Test
    public void test3GetList() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://localhost:4567/api/v1/test").build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        String expected = "{\"data\":[{\"timeLimit\":10,\"questions\":[],\"description\":\"d\",\"id\":\"" + id + "\"}],\"status\":\"success\"}";
        assertEquals(expected, responseBody);
    }

    @org.junit.Test
    public void test4GetId() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://localhost:4567/api/v1/test/id/" + id).build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        String expected = "{\"data\":{\"timeLimit\":10,\"questions\":[{\"question\":\"q\",\"correct\":\"c\",\"type\":\"openEnded\",\"explanation\":\"e\"}],\"description\":\"d\",\"id\":\"" + id + "\"},\"status\":\"success\"}";
        assertEquals(expected, responseBody);
    }

}