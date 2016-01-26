package fr.lhuet.home.domoweb;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by lhuet on 28/12/15.
 */
public class TeleinfoWebHandler {


    public static void getIinst(RoutingContext ctx) {
        EventBus send = ctx.vertx().eventBus().send("teleinfo-verticle", "getIinst", msg -> {
            if (msg.succeeded()) {
                ctx.response().end("Get I inst : " + msg.result().body().toString() + " !");
            }
            else {
                ctx.response().end("Error while getting Iinst : " + msg.cause().getMessage());
            }
        });
    }

    public static void getPapp(RoutingContext ctx) {
        ctx.response().end("Get P app !");
    }

    public static void getIndex(RoutingContext ctx) {
        ctx.response().end("get Index !");
    }
}
