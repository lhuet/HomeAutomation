package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * Created by lhuet on 30/12/15.
 */
public class TeleinfoVerticle extends AbstractVerticle{

    @Override
    public void start() throws Exception {
        MessageConsumer<String> consumer = vertx.eventBus().consumer("teleinfo-verticle");

        consumer.handler(msg -> {
           switch (msg.body()) {
               case "getIinst":
                   msg.reply("iInst Value");
                   break;
               default:
                   msg.reply("teleinfo request not correct !");
           }
        });

    }
}
