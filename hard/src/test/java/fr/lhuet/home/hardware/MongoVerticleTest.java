package fr.lhuet.home.hardware;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Created by lhuet on 26/01/16.
 */
@RunWith(VertxUnitRunner.class)
public class MongoVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {

        vertx = Vertx.vertx();
        JsonObject conf = new JsonObject();
        conf.put("mongo.host", "localhost");
        conf.put("mongo.port", "27017");
        conf.put("mongo.database", "teleinfo");
        DeploymentOptions options = new DeploymentOptions().setConfig(conf);
        vertx.deployVerticle(MongoVerticle.class.getName(), options, context.asyncAssertSuccess());

    }

    @Test
    public void Test(TestContext context) {
        final Async async = context.async();
        vertx.eventBus().send("teleinfo-getDataOfTheDay", "2014-02-15", event -> {
            if (event.succeeded()) {
                JsonArray result = (JsonArray) event.result().body();
                Assert.assertEquals(result.size(), 1428);
                async.complete();
            }
        });

    }

}
