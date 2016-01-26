package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 * Created by lhuet on 31/12/15.
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        vertx.deployVerticle("fr.lhuet.home.hardware.TeleinfoVerticle");
        DeploymentOptions options = new DeploymentOptions().setConfig(this.config());
        vertx.deployVerticle("fr.lhuet.home.hardware.Ds18b20Verticle", options);

    }
}
