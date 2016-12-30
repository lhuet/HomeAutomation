package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by lhuet on 31/12/15.
 */
public class MainVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        JsonObject conf = new JsonObject();
        conf.put("dhw-system.dhw", Vertx.class.getClassLoader().getResource("w1-dhw.data").getPath());
        conf.put("dhw-system.buffer", Vertx.class.getClassLoader().getResource("w1-buffer.data").getPath());
        conf.put("teleinfo.serialPort", "/dev/ttyUSB0");
        conf.put("elastic.host", "localhost");
        conf.put("elastic.port", 9200);
        conf.put("elastic.urlPrefix", "");
        DeploymentOptions options = new DeploymentOptions().setConfig(conf);
        Vertx.vertx().deployVerticle(MainVerticle.class.getName(), options);
    }


    @Override
    public void start() throws Exception {

        logger.debug("conf -> " + this.config());

        DeploymentOptions options = new DeploymentOptions().setConfig(this.config());

        vertx.deployVerticle(ElasticVerticle.class.getName(), options, event -> {
            vertx.deployVerticle(TeleinfoHardwareVerticle.class.getName(), options);
            vertx.deployVerticle(TeleinfoDataVerticle.class.getName());
            vertx.deployVerticle(DomesticHotWaterVerticle.class.getName(), options);
        });

//        JsonObject config = new JsonObject();
//        JsonArray hosts = new JsonArray().add(
//                new JsonObject().put("hostname", "localhost")
//                                .put("port", 9300)
//        );
//        config.put("address", "service.elasticsearch")
//                .put("transportAddresses", hosts)
//                .put("guice_binder", new JsonArray()
//                        .add("com.englishtown.vertx.elasticsearch.guice.ElasticSearchBinder"));
//
//        vertx.deployVerticle("java-guice:com.englishtown.vertx.elasticsearch.ElasticSearchServiceVerticle",
//                                new DeploymentOptions().setConfig(config),
//                                event -> {
//                                    // Wait ES service deployment before other verticles deployment
//
//                                    vertx.deployVerticle(TeleinfoHardwareVerticle.class.getName());
//                                    vertx.deployVerticle(TeleinfoDataVerticle.class.getName());
//
//                                    DeploymentOptions options = new DeploymentOptions().setConfig(this.config());
//                                    vertx.deployVerticle(DomesticHotWaterVerticle.class.getName(), options);
//                                });

    }
}
