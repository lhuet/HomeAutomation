package fr.lhuet.home.hardware;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.Instant;

/**
 * Created by lhuet on 31/12/15.
 */
public class DomesticHotWaterVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(DomesticHotWaterVerticle.class.getName());

    private static final int DHWSENSOR = 0;
    private static final int BUFFERSENSOR = 1;

    private static final String ES_INDEX = "temperature";
    private static final String ES_TYPE_DOC = "temp.ecs";

    private String w1DhwFile;
    private String w1BufferFile;

    private float dhwTemp;
    private float bufferTemp;

    @Override
    public void start(Future<Void> fut) throws Exception {

        w1DhwFile = this.config().getString("dhw-system.dhw");
        w1BufferFile = this.config().getString("dhw-system.buffer");

        logger.info("DHW File : " + w1DhwFile);
        logger.info("Buffer File : " + w1BufferFile);

        // Handler to serve the sensors values on the vertx event loop
        MessageConsumer<String> dhwConsumer = vertx.eventBus().consumer("dhw-temp");
        dhwConsumer.handler(event -> {
            JsonObject response = new JsonObject();
            switch (event.body()) {
                case "dhw":
                    response.put("dhw", this.dhwTemp);
                    event.reply(response);
                    break;
                case "buffer":
                    response.put("buffer", this.bufferTemp);
                    event.reply(response);
                    break;
                default:
                    response.put("dhw", this.dhwTemp);
                    response.put("buffer", this.bufferTemp);
                    event.reply(response);
            }
        });

        // Read the 2 temp sensors "immediately"
        readTemp(event -> {
            logger.debug("Temp sensor reading finished");
            indexTempIntoES();
            fut.complete();
        });
        // .. and continue refreshing the 2 temp. sensors every minute
        vertx.setPeriodic(60000, event -> {
            readTemp(event1 -> {
                logger.debug("Temp sensor reading finished");
                indexTempIntoES();
            });
        });

    }

    private void readTemp(Handler<AsyncResult> resultHandler) {

        Future<Void> dhwFut = Future.future();
        Future<Void> bufferFut = Future.future();

        readW1temp(w1DhwFile, DHWSENSOR, dhwFut.completer());
        readW1temp(w1BufferFile, BUFFERSENSOR, bufferFut.completer());

        CompositeFuture.all(dhwFut, bufferFut).setHandler( event -> {
            resultHandler.handle(Future.succeededFuture());
        });

    }

    private void readW1temp(String file, int sensor, Handler<AsyncResult<Void>> resultHandler) {
        // w1 file content like :
        //    ce 02 4b 46 7f ff 02 10 0c : crc=0c YES
        //    ce 02 4b 46 7f ff 02 10 0c t=44875
        vertx.fileSystem().readFile(file, (AsyncResult<Buffer> res) -> {
            String content = res.result().toString();
            if (content.contains("YES")) {
                // CRC ok -> extract Temp in deg C
                String[] temp = content.split("t=");
                switch (sensor) {
                    case DHWSENSOR:
                        this.dhwTemp = Float.valueOf(temp[1])/1000;
                        logger.info("Refreshing DHW Temp sensor value : " + this.dhwTemp);
                        break;
                    case BUFFERSENSOR:
                        this.bufferTemp = Float.valueOf(temp[1])/1000;
                        logger.info("Refreshing Buffer Temp sensor value : " + this.bufferTemp);
                        break;
                    default:
                        logger.error("readW1temp -> Bad sensor switch");
                        resultHandler.handle(Future.failedFuture("readW1temp -> Bad sensor switch"));
                }
                resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture("(One Wire bus error) Bad CRC on file " + file));
            }
        });
    }


    private void indexTempIntoES() {

        JsonObject docToindex = new JsonObject()
                                    .put("timestamp", Instant.now())
                                    .put("buffer", this.bufferTemp)
                                    .put("dhw", this.dhwTemp);
        JsonObject message = new JsonObject()
                                    .put("index", ES_INDEX)
                                    .put("type", ES_TYPE_DOC)
                                    .put("source", docToindex);

        vertx.eventBus().send("elastic.index", message, msg -> {
            if (msg.succeeded()) {
                logger.info(msg.result().body());
            }
            else {
                logger.error("indexTempIntoES Error ...", msg.cause());
            }
        });

    }

}
