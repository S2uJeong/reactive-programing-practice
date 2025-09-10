package practice.reactiveprograming;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

// Reactive Streams
@Slf4j
public class SchedulerEx {
    public static void main(String[] args) {
        Publisher<Integer> pub = sub ->   {
          sub.onSubscribe(new Subscription() {
            @Override
            public void request(long l) {
                log.info("request {}");
                sub.onNext(1);
                sub.onNext(2);
                sub.onNext(3);
                sub.onNext(4);
                sub.onNext(5);
                sub.onComplete();
            }

            @Override
            public void cancel() {

            }
          });
        };

//        Publisher<Integer> subOnPub = sub -> {
//            ExecutorService es = newSingleThreadExecutor(new CustomizableThreadFactory("subOn-"));
//            es.execute(() -> pub.subscribe(sub));
//        };

        Publisher<Integer> pubOnPub = sub -> {
          // subscribe 자체는 main Thread 에서 하고 싶은데 데이터 실제 흐름은 다른 쓰레드에서 하고 싶을 때
          pub.subscribe(new Subscriber<Integer>() {
            ExecutorService es = newSingleThreadExecutor(new CustomizableThreadFactory("pubOn-"));
            @Override
            public void onSubscribe(Subscription s) {
                sub.onSubscribe(s);
            }

            @Override
            public void onNext(Integer t) {
                es.execute(() -> sub.onNext(t));
            }

            @Override
            public void onError(Throwable throwable) {
                es.execute(() -> sub.onError(throwable));
                es.shutdown();
            }

            @Override
            public void onComplete() {
                es.execute(() -> sub.onComplete());
                es.shutdown();
            }
          });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
          @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
          }

          @Override
          public void onNext(Integer integer) {
            log.info("onNext : {}", integer);

          }

          @Override
          public void onError(Throwable throwable) {
            log.info("onError : {}",  throwable.getMessage());

          }

          @Override
          public void onComplete() {
            log.info("onComplete");
          }
        });
        log.info("exit");
    }
}
