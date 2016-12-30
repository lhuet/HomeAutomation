package fr.lhuet.home.domoweb;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by lhuet on 28/12/15.
 */
public class WebappVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(WebappVerticle.class);

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(WebappVerticle.class.getName());
    }

    @Override
    public void start(Future<Void> fut) {

        Router router = Router.router(vertx);

        router.get("/isAlive").handler((RoutingContext ctx) -> {
            ctx.response()
                    .putHeader("content-type", "application/json")
                    .end("{\"isAlive\":true}");
        });
        router.get("/rest/teleinfo/i").handler(TeleinfoWebHandler::getIinst);
        router.get("/rest/teleinfo/index").handler(TeleinfoWebHandler::getIndex);
        router.get("/rest/teleinfo/p").handler(TeleinfoWebHandler::getPapp);
        router.get("/rest/teleinfo").handler(TeleinfoWebHandler::getTeleinfo);

        router.get("/rest/dhw/dhw").handler(DomesticHotWaterWebHandler::getDhwTemperature);
        router.get("/rest/dhw/buffer").handler(DomesticHotWaterWebHandler::getBufferTemperature);
        router.get("/rest/dhw").handler(DomesticHotWaterWebHandler::getTemperature);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080, res -> {
            if (res.succeeded()) {
                logger.info("HTTP server started on port 8080");
                fut.complete();
            } else {
                fut.fail(res.cause());
            }
        });

    }

}