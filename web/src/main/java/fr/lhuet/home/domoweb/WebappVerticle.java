package fr.lhuet.home.domoweb;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by lhuet on 28/12/15.
 */
public class WebappVerticle extends AbstractVerticle {

    @Override
    public void start() {

        Router router = Router.router(vertx);

        router.get("/isAlive").handler((RoutingContext ctx) -> {
            ctx.response()
                    .putHeader("content-type", "application/json")
                    .end("{\"isAlive\":true}");
        });
        router.get("/rest/inst/i").handler(TeleinfoWebHandler::getIinst);
        router.get("/rest/inst/index").handler(TeleinfoWebHandler::getIndex);
        router.get("/rest/inst/p").handler(TeleinfoWebHandler::getPapp);

        router.get("/rest/dhw/dhw").handler(DomesticHotWaterWebHandler::getDhwTemperature);
        router.get("/rest/dhw/buffer").handler(DomesticHotWaterWebHandler::getBufferTemperature);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

    }

}