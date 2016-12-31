package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.Instant;

/**
 * Created by lhuet on 31/12/16.
 */
public class WeatherVerticle extends AbstractVerticle{


    private static Logger logger = LoggerFactory.getLogger(WeatherVerticle.class);

    private static final String ES_TYPE_DOC = "record";
    private static final String ES_INDEX = "weather";

    private String bmp085LinuxVirtualFsDir;
    private float bmp085Temp;
    private float bmp085Pressure;




    @Override
    public void start() throws Exception {

        bmp085LinuxVirtualFsDir = this.config().getString("weather.bmp085Dir");

        if (bmp085LinuxVirtualFsDir != null) {

            // Read sensor on startup
            readBmp085(event -> {
                indexToES();
            });

            // ... and then every minute
            vertx.setPeriodic(60000, res -> {
               readBmp085(event -> {
                   indexToES();
               });
            });

        }

    }

    private void readBmp085(Handler<AsyncResult> ar) {

        Future<Void> startFuture = Future.future();

        Future<Void> fut1 = Future.future();
        vertx.fileSystem().readFile(bmp085LinuxVirtualFsDir+ "/temp0_input", result -> {
            if (result.succeeded()) {
                bmp085Temp = Float.valueOf(result.result().toString())/10;
                logger.info("BMP085 Temp = " + bmp085Temp);
            }
            else {
                logger.error("Error while reading bmp085 temperature", result.cause());
            }
            fut1.complete();
        });

        fut1.compose(event -> {

            vertx.fileSystem().readFile(bmp085LinuxVirtualFsDir+ "/pressure0_input", result -> {
                if (result.succeeded()) {
                    bmp085Pressure = Float.valueOf(result.result().toString())/100;
                    logger.info("BMP085 Pressure = " + bmp085Pressure);
                } else {
                    logger.error("Error while reading bmp085 pressure", result.cause());
                }
            });

        }, startFuture);

        ar.handle(startFuture);

    }


    private void indexToES() {

        JsonObject docToindex = new JsonObject()
                .put("timestamp", Instant.now())
                .put("tempCave", this.bmp085Temp)
                .put("pressure", this.bmp085Pressure);
        JsonObject message = new JsonObject()
                .put("index", ES_INDEX)
                .put("type", ES_TYPE_DOC)
                .put("id", "sensor_" + Instant.now().toEpochMilli())
                .put("source", docToindex);

        vertx.eventBus().send("elastic.index", message, msg -> {
            if (msg.succeeded()) {
                logger.info(msg.result().body());
            }
            else {
                logger.error("indexWheaterIntoES Error ...", msg.cause());
            }
        });

    }

}
