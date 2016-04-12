package com.github.yeriomin.andtest.server;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSend {

    static private final String FIELD_STATUS = "status";
    static private final String FIELD_DATA = "data";
    static private final String FIELD_CODE = "code";
    static private final String FIELD_MESSAGE = "message";

    static private final String STATUS_SUCCESS = "success";
    static private final String STATUS_FAIL = "fail";
    static private final String STATUS_ERROR = "error";

    static public JSONObject success() {
        JSONObject object = new JSONObject();
        object.put(FIELD_STATUS, STATUS_SUCCESS);
        return object;
    }

    static public JSONObject success(JSONObject data) {
        JSONObject object = success();
        object.put(FIELD_DATA, data);
        return object;
    }

    static public JSONObject success(JSONArray data) {
        JSONObject object = success();
        object.put(FIELD_DATA, data);
        return object;
    }

    static public JSONObject fail(JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(FIELD_STATUS, STATUS_FAIL);
        object.put(FIELD_DATA, data);
        return object;

    }

    static public JSONObject error(String message) {
        JSONObject object = new JSONObject();
        object.put(FIELD_STATUS, STATUS_ERROR);
        object.put(FIELD_MESSAGE, message);
        return object;
    }

    static public JSONObject error(String message, int code) {
        JSONObject object = error(message);
        object.put(FIELD_CODE, code);
        return object;
    }

    static public JSONObject error(String message, int code, JSONObject data) {
        JSONObject object = error(message, code);
        object.put(FIELD_DATA, data);
        return object;
    }

    static public JSONObject error(String message, JSONObject data) {
        JSONObject object = error(message);
        object.put(FIELD_DATA, data);
        return object;
    }
}
