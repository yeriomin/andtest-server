package com.github.yeriomin.andtest.server;

import com.github.yeriomin.andtest.core.Test;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class TestGet extends RequestProcessor {

    public TestGet(Request request, Response response) {
        super(request, response);
    }

    public JSONObject getResponseObject() {
        String id = this.request.params(":id");
        return (null != id && id.length() > 0)
                ? JSend.success(getTest(id))
                : JSend.success(getTestList(this.request));
    }

    private JSONObject getTest(String id) {
        return new DbHandler().getOne(id).toJSONObject();
    }

    private JSONArray getTestList(Request request) {
        DbHandler db = new DbHandler();
        if (null != request.queryParams("searchString") && request.queryParams("searchString").length() > 0) {
            db.setSearchString(request.queryParams("searchString"));
        }
        if (null != request.queryParams("limit") && request.queryParams("limit").length() > 0) {
            db.setLimit(Integer.parseInt(request.queryParams("limit")));
        }
        if (null != request.queryParams("skip") && request.queryParams("skip").length() > 0) {
            db.setSkip(Integer.parseInt(request.queryParams("skip")));
        }
        ArrayList<Test> tests = db.getAll();
        JSONArray responseArray = new JSONArray();
        for (Test test: tests) {
            test.getQuestions().clear();
            responseArray.put(test.toJSONObject());
        }
        return responseArray;
    }
}
