package com.github.yeriomin.andtest.server;

import org.everit.json.schema.ValidationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static spark.Spark.*;

public class Server {

    private static final String API_CONTEXT = "/api/v1";

    public static void main(String[] args) {
        Properties properties = getDefaultConfig();
        if (args.length > 0 && new File(args[0]).exists()) {
            try (InputStream inputStream = new FileInputStream(args[0])) {
                properties.clear();
                properties.load(inputStream);
            } catch (IOException e) {
                System.err.println("Could not read " + args[0]);
            }
        }
        DbHandler.setConfig(properties);
        port(Integer.parseInt(properties.getProperty("port")));
        get(API_CONTEXT + "/test", (req, res) -> new TestGet(req, res).getResponseString());
        get(API_CONTEXT + "/test/id/:id", (req, res) -> new TestGet(req, res).getResponseString());
        put(API_CONTEXT + "/test", (req, res) -> new TestPut(req, res).getResponseString());
        after((request, response) -> response.type("application/json"));
        exception(ValidationException.class, (e, request, response) -> {
            int code = 400;
            response.status(code);
            response.body(JSend.error("Test file is invalid", code).toString());
        });
        exception(NotFoundException.class, (e, request, response) -> {
            int code = 404;
            response.status(code);
            response.body(JSend.error("Not found", code).toString());
        });
        exception(RuntimeException.class, (e, request, response) -> {
            int code = 500;
            response.status(code);
            response.body(JSend.error("General error: " + e.getClass() + " " + e.getMessage(), code).toString());
            e.printStackTrace();
        });
    }

    private static Properties getDefaultConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = Server.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Could not read /config.properties");
        }
        String host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
        if (null != host) {
            properties.setProperty("mongo.host", host);
            properties.setProperty("mongo.port", System.getenv("OPENSHIFT_MONGODB_DB_PORT"));
            properties.setProperty("mongo.dbname", System.getenv("OPENSHIFT_APP_NAME"));
            properties.setProperty("mongo.username", System.getenv("OPENSHIFT_MONGODB_DB_USERNAME"));
            properties.setProperty("mongo.password", System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD"));
        }
        return properties;
    }
}
