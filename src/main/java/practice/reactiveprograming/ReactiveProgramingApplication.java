package practice.reactiveprograming;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ReactiveProgramingApplication {
    @RestController
    public static class Controller {
        @RequestMapping("/hello")
        public Publisher<String> hello(String name) {
            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> subscriber) {
                    subscriber.onSubscribe(new Subscription() {
                        @Override
                        public void request(long l) {
                            subscriber.onNext("Hello " + name);
                            subscriber.onNext("Hello2 " + name);
                            subscriber.onNext("Hello3 " + name);
                            subscriber.onNext("Hello4 " + name); // 어 이것만 찍히네 ?
                            subscriber.onComplete();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            };
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgramingApplication.class, args);
    }

}
