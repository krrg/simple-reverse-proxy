import io.vertx.core.Vertx;

/**
 * Created by krr428 on 2/27/16.
 */
public class Main {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        vertx.createHttpServer();

        vertx.deployVerticle(new ReverseProxyServer());

    }

}
