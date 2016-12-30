package fr.lhuet.home.hardware;

import fr.lhuet.home.hardware.pojo.TeleinfoData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.Instant;

/**
 * Created by lhuet on 11/08/16.
 */
public class TeleinfoDataVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(TeleinfoDataVerticle.class);

    private static final String ES_INDEX = "teleinfo";
    private static final String ES_TYPE_DOC = "trame";

    private TeleinfoData data;

    @Override
    public void start() throws Exception {

        // Init TeleinfoData
        data = new TeleinfoData();

        // Consume each teleinfo chunk data
        vertx.eventBus().consumer("teleinfo.trame", (Message<JsonObject> msg) -> {
            dataUpdate(msg.body());
        });

        // Set handler to flush data to ElasticSearch every minute
        vertx.setPeriodic(60000, event -> {
            data.setPmoy((float)data.getSumpapp()/data.getNbdata());
            data.setImoy((float)data.getSumiinst()/data.getNbdata());
            flushToES();
            // RAZ moy and max values
            data.raz();
        });

        // Expose instant values
        vertx.eventBus().consumer("teleinfo.instantValue", request -> {
            JsonObject response = new JsonObject();
            switch (request.body().toString()) {
                case "getIinst":
                    response.put("IINST", data.getIinst());
                    request.reply(response);
                    break;
                case "getPapp":
                    response.put("PAPP", data.getPapp());
                    request.reply(response);
                    break;
                case "getIndex":
                    response.put("INDEX", data.getIndexcpt());
                    request.reply(response);
                    break;
                default:
                    response.put("IINST", data.getIinst());
                    response.put("PAPP", data.getPapp());
                    response.put("INDEX", data.getIndexcpt());
                    request.reply(response);
            }
        });

    }

    private void dataUpdate(JsonObject teleinfo) {
        logger.debug("Update teleinfo data" + teleinfo);

        data.setIndexcpt(teleinfo.getInteger("BASE"));
        data.setNbdata(data.getNbdata() + 1);
        data.setSumiinst(data.getSumiinst() + teleinfo.getInteger("IINST"));
        data.setIinst(teleinfo.getInteger("IINST"));
        data.setSumpapp(data.getSumpapp() + teleinfo.getInteger("PAPP"));
        data.setPapp(teleinfo.getInteger("PAPP"));
        if (teleinfo.getInteger("PAPP") > data.getPmax()) {
            data.setPmax(teleinfo.getInteger("PAPP"));
        }
        if (teleinfo.getInteger("IINST") > data.getImax()) {
            data.setImax(teleinfo.getInteger("IINST"));
        }
    }

    private void flushToES() {

        JsonObject docToindex = new JsonObject()
                .put("timestamp", Instant.now())
                .put("pmax", data.getPmax())
                .put("imax", data.getImax())
                .put("imoy", data.getImoy())
                .put("pmoy", data.getPmoy())
                .put("indexcpt", data.getIndexcpt());

        JsonObject message = new JsonObject()
                .put("index", ES_INDEX)
                .put("type", ES_TYPE_DOC)
                .put("id", "ti_" + Instant.now().toEpochMilli())
                .put("source", docToindex);

        vertx.eventBus().send("elastic.index", message, msg -> {
            if (msg.succeeded()) {
                logger.info(msg.result().body());
            }
            else {
                logger.error(msg.cause().getMessage());
            }
        });

    }

}
