import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;

/**
 * Created by krr428 on 2/27/16.
 */
public class ReverseProxyServer extends AbstractVerticle {

    @Override
    public void start() {

        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(routingContext -> {

            final HttpServerResponse serverResponse = routingContext.response();
            final HttpServerRequest serverRequest = routingContext.request();

            final String host = "google.com";

            HttpClient client = vertx.createHttpClient(new HttpClientOptions().setDefaultHost("google.com").setDefaultPort(80));

            HttpClientRequest clientRequest = client.request(serverRequest.method(), serverRequest.uri());
            clientRequest.headers().addAll(serverRequest.headers().set("Host", host));

            Buffer slowDownstreamBuffer = Buffer.buffer();
            serverRequest.handler(buffer -> {
                slowDownstreamBuffer.appendBuffer(buffer);
            });

            serverRequest.endHandler(i -> {
                clientRequest.end(slowDownstreamBuffer);
            });


            clientRequest.handler(httpClientResponse -> {

                serverResponse.headers().addAll(httpClientResponse.headers());
                serverResponse.setChunked(true);

                httpClientResponse.handler(buffer -> {
                    serverResponse.write(buffer);
                });

                httpClientResponse.endHandler(i -> {
                    serverResponse.end();
                });
            });


        });

        httpServer.requestHandler(router::accept).listen(9000);

    }
}
