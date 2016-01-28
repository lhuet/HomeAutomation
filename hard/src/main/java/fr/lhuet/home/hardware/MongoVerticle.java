package fr.lhuet.home.hardware;

import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by lhuet on 25/01/16.
 */
public class MongoVerticle extends AbstractVerticle{

    private JsonObject mongoConfig;
    private String teleinfoCollection;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        JsonObject config = Vertx.currentContext().config();

        String host = config.getString("mongo.host");
        if (host == null) {
            host = "localhost";
        }
        String port = config.getString("mongo.port");
        if (port == null) {
            port = "27017";
        }
        String db = config.getString("mongo.database");
        if (db == null) {
            db = "db";
        }
        teleinfoCollection = config.getString("mongo.collection");
        if (teleinfoCollection == null) {
            teleinfoCollection = "teleinfo";
        }

        String uri = "mongodb://" + host + ":" + port;
        mongoConfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);


        vertx.eventBus().consumer("teleinfo-getDataOfTheDay", event -> {
            // Date passée chaine texte dans le body du message sous la forme "2014-02-15"
            getTeleinfoDataOfTheDay(event.body().toString(), teleinfoRes -> {
                event.reply(teleinfoRes.result());
            });
        });

        startFuture.complete();
    }


    private void getTeleinfoDataOfTheDay(String date, Handler<AsyncResult<JsonArray>> resultHandler) {

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoConfig);

        String query = "{\"datetime\":{" +
                "\"$gte\":{\"$date\":\"" +date + "T00:00:00.000Z\"}," +
                "\"$lte\":{\"$date\":\"" +date + "T23:59:59.999Z\"}" +
                "}}";
        JsonObject jsonQuery = new JsonObject(query);

        mongoClient.find(teleinfoCollection, jsonQuery, res -> {
            if (res.succeeded()) {
                JsonArray result = new JsonArray();
                for (JsonObject item : res.result()) {
                    JsonObject data = new JsonObject();
                    data.put("datetime", item.getJsonObject("datetime"));
                    data.put("indexcpt", item.getInteger("indexcpt"));
                    data.put("imax", item.getInteger("imax"));
                    data.put("pmax", item.getInteger("pmax"));
                    data.put("imoy", item.getFloat("imoy"));
                    data.put("pmoy", item.getFloat("pmoy"));
                    result.add(data);
                }
                resultHandler.handle(Future.succeededFuture(result));
            }
            else {
                resultHandler.handle(Future.failedFuture("Mongo query error"));
            }
        });

    }


}
