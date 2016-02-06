package fr.lhuet.home.domoweb;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by lhuet on 28/01/16.
 */
@RunWith(VertxUnitRunner.class)
public class TeleinfoWebTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {

        vertx = Vertx.vertx();
        vertx.deployVerticle(WebappVerticle.class.getName());
    }

    @Test
    public void test(TestContext context) {
        HttpClient clientHttp = vertx.createHttpClient();
        clientHttp.getNow(8080, "localhost", "/rest/teleinfo/2015/12/02", response -> {
            System.out.println("test");
            System.out.println("Http Response code : " + response.statusCode());
            System.out.println("test");
            context.asyncAssertSuccess();
        });
        System.out.println("test");

    }


}
