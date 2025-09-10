package practice.reactiveprograming;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class IntervalEx {

  public static void main(String[] args) {
    Publisher<Integer> pub = sub -> {
      sub.onSubscribe(new Subscription() {
        int no = 0;

        @Override
        public void request(long l) {
          ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
          exec.scheduleAtFixedRate(() -> {
            sub.onNext(no++);
          }, 0, 300, TimeUnit.MICROSECONDS);
        }

        @Override
        public void cancel() {
        }
      });
    };
    pub.subscribe(new Subscriber<Integer>() {
      @Override
      public void onSubscribe(Subscription s) {
          log.info("onSubscribe");
          s.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(Integer i) {
        log.info("onNext : {}", i);
      }

      @Override
      public void onError(Throwable t) {
        log.error("onError", t);

      }

      @Override
      public void onComplete() {

      }
    });
  }

}
