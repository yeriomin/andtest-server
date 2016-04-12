package com.github.yeriomin.andtest.server;

import com.github.yeriomin.andtest.core.JSONConverter;
import com.github.yeriomin.andtest.core.Test;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class DbHandler {

    private static Properties config;
    private static DB db;
    private static DBCollection collection;

    private String searchString;
    private int limit;
    private int skip;

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public static DBCollection getCollection() {
        if (null == collection) {
            try {
                String host = config.getProperty("mongo.host");
                int port = Integer.parseInt(config.getProperty("mongo.port"));
                MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
                MongoClient mongoClient = new MongoClient(new ServerAddress(host, port), mongoClientOptions);
                mongoClient.setWriteConcern(WriteConcern.SAFE);
                String dbname = config.getProperty("mongo.dbname");
                db = mongoClient.getDB(dbname);
                String username = config.getProperty("mongo.username");
                String password = config.getProperty("mongo.password");
                if (username.length() > 0 && !db.authenticate(username, password.toCharArray())) {
                    throw new RuntimeException("Could not to authenticate with MongoDB");
                }
            } catch (UnknownHostException e) {
                throw new RuntimeException("UnknownHostException", e);
            }
            collection = db.getCollection("test");
        }
        return collection;
    }

    public String put(JSONObject object) {
        BasicDBObject doc = new BasicDBObject(JSONConverter.toMap(object));
        getCollection().insert(doc);
        ObjectId id = (ObjectId) doc.get("_id");
        return id.toString();
    }

    public Test getOne(String id) {
        BasicDBObject object = (BasicDBObject) getCollection().findOne(new BasicDBObject("_id", new ObjectId(id)));
        if (null == object) {
            throw new NotFoundException();
        }
        Test test = new Test(object);
        test.setId(object.get("_id").toString());
        return test;
    }

    public ArrayList<Test> getAll() {
        DBCollection c = getCollection();
        DBCursor cursor = null == this.searchString ? c.find() : c.find(getQuery(searchString));
        if (this.limit > 0) {
            cursor.limit(this.limit);
        }
        if (this.skip > 0) {
            cursor.skip(this.skip);
        }
        return CursorToList(cursor);
    }

    public void accept(String id) {
        getCollection().update(new BasicDBObject("_id", new ObjectId(id)), new BasicDBObject("$set", new BasicDBObject("moderated", true)));
    }

    private static DBObject getQuery(String searchString) {
        BasicDBObject queryName = new BasicDBObject("name", new BasicDBObject("$regex", searchString));
        BasicDBObject queryDescription = new BasicDBObject("description", new BasicDBObject("$regex", searchString));
        return QueryBuilder.start().and(queryName, queryDescription).get();
    }

    private static ArrayList<Test> CursorToList(DBCursor dbObjects) {
        ArrayList<Test> tests = new ArrayList<>();
        while (dbObjects.hasNext()) {
            BasicDBObject dbObject = (BasicDBObject) dbObjects.next();
            Test test = new Test(dbObject);
            test.setId(dbObject.get("_id").toString());
            tests.add(test);
        }
        return tests;
    }

    public static void setConfig(Properties properties) {
        config = properties;
    }

    public static Properties getConfig() {
        return config;
    }
}
