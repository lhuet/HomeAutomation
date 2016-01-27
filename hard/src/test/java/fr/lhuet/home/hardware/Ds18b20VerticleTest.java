package fr.lhuet.home.hardware;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by lhuet on 24/01/16.
 */
@RunWith(VertxUnitRunner.class)
public class Ds18b20VerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        JsonObject conf = new JsonObject();
        conf.put("dhw-system.dhw", getClass().getClassLoader().getResource("w1-dhw.data").getPath());
        conf.put("dhw-system.buffer", getClass().getClassLoader().getResource("w1-buffer.data").getPath());
        DeploymentOptions options = new DeploymentOptions().setConfig(conf);
        vertx.deployVerticle(Ds18b20Verticle.class.getName(), options, context.asyncAssertSuccess());
    }


    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void ds18b20ReadDhwTempTest(TestContext context) throws InterruptedException {

        final Async async = context.async();
        vertx.eventBus().send("dhw-temp", "dhw", event -> {
            if (event.succeeded()) {
                Assert.assertEquals(55.937f, event.result().body());
                async.complete();
            }
        });
    }

    @Test
    public void ds18b20ReadDhwBufferTest(TestContext context) throws InterruptedException {
        final Async async = context.async();
        vertx.eventBus().send("dhw-temp", "buffer", event -> {
            if (event.succeeded()) {
                Assert.assertEquals(58.437f, event.result().body());
                async.complete();
            }
        });
    }

}
