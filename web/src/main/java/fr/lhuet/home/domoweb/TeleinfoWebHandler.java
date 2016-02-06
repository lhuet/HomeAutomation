package fr.lhuet.home.domoweb;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by lhuet on 28/12/15.
 */
public class TeleinfoWebHandler {


    public static void getIinst(RoutingContext ctx) {
        ctx.vertx().eventBus().send("teleinfo-verticle", "getIinst", msg -> {
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

    public static void getTeleinfoDataByDay(RoutingContext ctx) {
        String dateRequest = ctx.request().getParam("year") + "-" +
                ctx.request().getParam("month") + "-" +
                ctx.request().getParam("day");
        System.out.println("dateRequest : " + dateRequest);
        ctx.vertx().eventBus().send("teleinfo-getDataOfTheDay", dateRequest, res -> {
            if (res.succeeded()) {
                System.out.println("response ok");
                ctx.response().write(res.result().body().toString());
            } else {
                ctx.response().setStatusCode(500).end(res.cause().getMessage());
            }
        });
    }
}
