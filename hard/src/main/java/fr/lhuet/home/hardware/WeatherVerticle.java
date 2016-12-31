package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by lhuet on 31/12/16.
 */
public class WeatherVerticle extends AbstractVerticle{

    private static Logger logger = LoggerFactory.getLogger(WeatherVerticle.class);

    private String bmp085LinuxVirtualFsDir;
    private float bmp085Temp;
    private float bmp085Pressure;

    @Override
    public void start() throws Exception {

        bmp085LinuxVirtualFsDir = this.config().getString("weather.bmp085Dir");

        if (bmp085LinuxVirtualFsDir != null) {

            vertx.setPeriodic(5000, res -> {
               readBmp085();
            });

        }

    }

    private void readBmp085() {

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


    }

}
