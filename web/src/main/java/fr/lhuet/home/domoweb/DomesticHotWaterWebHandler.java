package fr.lhuet.home.domoweb;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by lhuet on 29/12/15.
 */
public class DomesticHotWaterWebHandler {

    private static Logger logger = LoggerFactory.getLogger(DomesticHotWaterWebHandler.class);

    public static void getBufferTemperature(RoutingContext ctx) {

        ctx.vertx().eventBus().send("dhw-temp", "buffer", response -> {
            ctx.response().end(response.result().body().toString());
        });

    }

    public static void getDhwTemperature(RoutingContext ctx) {

        ctx.vertx().eventBus().send("dhw-temp", "dhw", response -> {
            ctx.response().end(response.result().body().toString());
        });

    }

    public static void getTemperature(RoutingContext ctx) {

        ctx.vertx().eventBus().send("dhw-temp", "", response -> {
            ctx.response().end(response.result().body().toString());
        });
    }

}
