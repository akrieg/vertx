

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public class PerfServer extends AbstractVerticle {

  public void start() {
	  System.out.println("Starting Server...");
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        // Just return OK
        req.response().end();

        // If you want to serve a real file uncomment this and comment previous line
        //req.response().sendFile("httpperf/foo.html");
//        vertx.fileSystem().readFile("httpperf/foo.html", new AsyncResultHandler<Buffer>() {
//          public void handle(AsyncResult<Buffer> ar) {
//            req.response().putHeader("Content-Length", ar.result.length());
//            req.response().putHeader("Content-Type", "text/html");
//            req.response().end(ar.result);
//          }
//        });
      }
//    }).listen(8088, "172.17.177.71");
    }).listen(8088, "localhost");
  }
  
  public static void main(String[] args) {
	  io.vertx.core.Starter.main(new String[]{"run" , "PerfServer", "-instances", "6"});
  }
}

