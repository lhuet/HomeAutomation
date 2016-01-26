package fr.lhuet.home.domoweb;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by lhuet on 29/12/15.
 */
public class DomesticHotWaterWebHandler {

    public static void getBufferTemperature(RoutingContext ctx) {
        ctx.response().end("get Buffer Temp");
    }

    public static void getDhwTemperature(RoutingContext ctx) {
        ctx.response().end("get DHW Temp ");
    }

}
