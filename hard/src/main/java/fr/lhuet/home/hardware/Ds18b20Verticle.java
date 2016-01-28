package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by lhuet on 31/12/15.
 */
public class Ds18b20Verticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(Ds18b20Verticle.class.getName());

    private static final int DHWSENSOR = 0;
    private static final int BUFFERSENSOR = 1;

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
            switch (event.body()) {
                case "dhw":
                    event.reply(this.dhwTemp);
                    break;
                case "buffer":
                    event.reply(this.bufferTemp);
                    break;
                default:
                    event.reply(Double.valueOf(null));
            }
        });

        // Read the 2 temp sensors "immediately"
        readTemp(event1 -> {
            logger.debug("Temp sensor reading finished");
            fut.complete();
        });
        // .. and continue refreshing the 2 temp. sensors every minute
        vertx.setPeriodic(60000, event -> {
            readTemp(event1 -> {
                logger.debug("Temp sensor reading finished");
            });
        });

    }

    private void readTemp(Handler<AsyncResult> resultHandler) {

        readW1temp(w1DhwFile, DHWSENSOR, event1 -> {
            readW1temp(w1BufferFile, BUFFERSENSOR, event2 -> {
                resultHandler.handle(Future.succeededFuture());
            });
        });
    }

    private void readW1temp(String file, int sensor, Handler<AsyncResult> handlerResult) {
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
                        logger.debug("Refreshing DHW Temp sensor value : " + this.dhwTemp);
                        break;
                    case BUFFERSENSOR:
                        this.bufferTemp = Float.valueOf(temp[1])/1000;
                        logger.debug("Refreshing Buffer Temp sensor value : " + this.bufferTemp);
                        break;
                    default:
                        logger.error("readW1temp -> Bad sensor switch");
                        handlerResult.handle(Future.failedFuture("readW1temp -> Bad sensor switch"));
                }
                handlerResult.handle(Future.succeededFuture());
            } else {
                handlerResult.handle(Future.failedFuture("(One Wire bus error) Bad CRC on file " + file));
            }
        });
    }


}
