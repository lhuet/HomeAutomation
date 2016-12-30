package fr.lhuet.home.hardware;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by lhuet on 29/12/16.
 */
public class ElasticVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(ElasticVerticle.class);

    private RestClient esClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        String elasticHost = this.config().getString("elastic.host");
        int elasticPort = this.config().getInteger("elastic.port");
        String elasticUrlPrefix = this.config().getString("elastic.urlPrefix");

        vertx.executeBlocking(future -> {
            // ElasticSearch client initialization
            esClient = RestClient.builder(
                    new HttpHost(elasticHost, elasticPort, "http")).build();
            future.complete();
        }, res -> {
            // ElasticSearch index message from event bus
            vertx.eventBus().consumer("elastic.index", (Message<JsonObject> msg) -> {
                logger.info(msg.body());
                String index = msg.body().getString("index");
                String docType = msg.body().getString("type");
                String idDoc = msg.body().getString("id");
                String docToIndex = msg.body().getJsonObject("source").toString();
                HttpEntity entity = new NStringEntity(docToIndex, ContentType.APPLICATION_JSON);

                vertx.executeBlocking(futIndex -> {
                    JsonObject response = new JsonObject();
                    try {
                        Response indexResponse;
                        if (idDoc == null) {
                            // Id auto-generated
                            indexResponse = esClient.performRequest(
                                    "POST",
                                    elasticUrlPrefix + "/" + index + "/" + docType,
                                    Collections.<String, String>emptyMap(),
                                    entity
                            );
                        }
                        else {
                            indexResponse = esClient.performRequest(
                                    "PUT",
                                    elasticUrlPrefix + "/" + index + "/" + docType + "/" + idDoc,
                                    Collections.<String, String>emptyMap(),
                                    entity
                            );
                        }
                        // TODO Manage ES Client return status / error
                        response.put("StatusCode", indexResponse.getStatusLine().getStatusCode());

                    } catch (IOException e) {
                        //TODO Better exception catching
                        e.printStackTrace();
                    }
                    futIndex.complete(response);
                }, resIndex -> {
                    // Send response to event bus when ES
                    msg.reply(resIndex.result());
                });


            });

            startFuture.complete();
        });

    }


    @Override
    public void stop() throws Exception {
        esClient.close();
    }
}
