package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by lhuet on 31/12/15.
 */
public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        JsonObject conf = new JsonObject();
        conf.put("dhw-system.dhw", Vertx.class.getClassLoader().getResource("w1-dhw.data").getPath());
        conf.put("dhw-system.buffer", Vertx.class.getClassLoader().getResource("w1-buffer.data").getPath());
        DeploymentOptions options = new DeploymentOptions().setConfig(conf);
        Vertx.vertx().deployVerticle(MainVerticle.class.getName(), options);
    }


    @Override
    public void start() throws Exception {

        vertx.deployVerticle("fr.lhuet.home.hardware.TeleinfoVerticle");
        DeploymentOptions options = new DeploymentOptions().setConfig(this.config());
        vertx.deployVerticle("fr.lhuet.home.hardware.Ds18b20Verticle", options);

    }
}
