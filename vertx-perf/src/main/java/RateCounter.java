

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

public class RateCounter extends AbstractVerticle implements Handler<Message<Integer>> {

  private long last;

  private long count;

  private long start;

  private long totCount;

  public void handle(Message<Integer> msg) {
    if (last == 0) {
      last = start = System.currentTimeMillis();
    }
    count += msg.body();
    totCount += msg.body();
  }

  public void start() {
    vertx.eventBus().consumer("rate-counter", this);
    vertx.setPeriodic(3000, new Handler<Long>() {
      public void handle(Long id) {
        if (last != 0) {
          long now = System.currentTimeMillis();
          double rate = 1000 * (double)count / (now - last);
          double avRate = 1000 * (double)totCount / (now - start);
          count = 0;
          System.out.println((now - start) + " Rate: count/sec: " + rate + " Average rate: " + avRate);
          last = now;
        }
      }
    });
  }

  public static void main(String args[]) {
	  io.vertx.core.Starter.main(new String[]{"run" , "RateCounter", "-cluster"});
  }

}
