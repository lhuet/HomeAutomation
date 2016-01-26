package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by lhuet on 25/01/16.
 */
public class MongoVerticle extends AbstractVerticle{

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        JsonObject config = Vertx.currentContext().config();

        String uri = config.getString("mongo_uri");
        if (uri == null) {
            uri = "mongodb://localhost:27017";
        }
        String db = config.getString("mongo_db");
        if (db == null) {
            db = "teleinfo";
        }

        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);

        MongoClient client = MongoClient.createShared(vertx, mongoconfig);

    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {

    }
}
