package fr.lhuet.home.domoweb;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by lhuet on 28/12/15.
 */
public class TeleinfoWebHandler {


    public static void getIinst(RoutingContext ctx) {
        ctx.vertx().eventBus().send("teleinfo.instantValue", "getIinst", msg -> {
            if (msg.succeeded()) {
                ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(msg.result().body().toString());
            }
            else {
                ctx.response().end("Error while getting Iinst : " + msg.cause().getMessage());
            }
        });
    }

    public static void getPapp(RoutingContext ctx) {
        ctx.vertx().eventBus().send("teleinfo.instantValue", "getPapp", msg -> {
            if (msg.succeeded()) {
                ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(msg.result().body().toString());
            }
            else {
                ctx.response().end("Error while getting Papp : " + msg.cause().getMessage());
            }
        });;
    }

    public static void getIndex(RoutingContext ctx) {
        ctx.vertx().eventBus().send("teleinfo.instantValue", "getIndex", msg -> {
            if (msg.succeeded()) {
                ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(msg.result().body().toString());
            }
            else {
                ctx.response().end("Error while getting Index : " + msg.cause().getMessage());
            }
        });;
    }

    public static void getTeleinfo(RoutingContext ctx) {
        ctx.vertx().eventBus().send("teleinfo.instantValue", "", msg -> {
            if (msg.succeeded()) {
                ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(msg.result().body().toString());
            }
            else {
                ctx.response().end("Error while getting Teleinfo : " + msg.cause().getMessage());
            }
        });;
    }

}
