package com.github.yeriomin.andtest.server;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

abstract public class RequestProcessor {

    protected Request request;
    protected Response response;

    public RequestProcessor(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    abstract public JSONObject getResponseObject();

    public String getResponseString() {
        LoggerFactory.getLogger(RequestProcessor.class).info(
                "Processing a {} request to {} from {} ({})",
                this.request.requestMethod(),
                this.request.uri(),
                this.request.ip(),
                this.request.host()
        );
        return getResponseObject().toString();
    }
}
