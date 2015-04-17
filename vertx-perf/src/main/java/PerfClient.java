

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.VoidHandler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;

public class PerfClient extends AbstractVerticle implements Handler<HttpClientResponse> {

  private HttpClient client;

  private long start;

  private int count = 0;

  // This determines the degree of pipelining
  private static final int CREDITS_BATCH = 2000;

  // Number of connections to create
  private static final int MAX_CONNS = 10;

  private int requestCredits = CREDITS_BATCH;

  private EventBus eb;

  public void handle(HttpClientResponse response) {
    if (response.statusCode() != 200) {
      throw new IllegalStateException("Invalid response");
    }
    response.endHandler(new VoidHandler() {
      public void handle() {
        count++;
        if (count % 2000 == 0) {
          eb.send("rate-counter", count);
          count = 0;
        }
        requestCredits++;
        makeRequest();
      }
    });
  }

  public void start() {
    eb = vertx.eventBus();
    
    HttpClientOptions o = new HttpClientOptions();
    o.setMaxPoolSize(MAX_CONNS);
    o.setDefaultHost("localhost");
    o.setDefaultPort(8088);
    client = vertx.createHttpClient(o);
    makeRequest();
  }

  private void makeRequest() {
    if (start == 0) {
      start = System.currentTimeMillis();
    }
    while (requestCredits > 0) {
      client.getNow("/test/", this);
      requestCredits--;
    }
  }

  public static void main(String args[]) {
	  io.vertx.core.Starter.main(new String[]{"run" , "PerfClient", "-cluster", "-instances", "6"});
  }
}

