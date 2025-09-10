package practice.reactiveprograming;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
@Slf4j
public class FluxScEx {

  public static void main(String[] args) throws InterruptedException {
    Flux.interval(Duration.ofMillis(200)) // demon Thread
        .take(10)
        .subscribe(s -> log.info("on Next : {}" ,s));
    log.info("exit");
    TimeUnit.SECONDS.sleep(5);  // userThread
    // user/demon Thread
    // demon : user가 하나도 없으면 자동 종료됨 (demon이 많아도 같이 종료됨)
    // user : user 하나라도 있으면 종료 안됨 -> demon도 종료 안됨
    Flux.range(1, 10) // Flux : pub
        .publishOn(Schedulers.newSingle("pub"))
        .log()
        .subscribeOn(Schedulers.newSingle("sub"))
        .subscribe(System.out::println); // onNext에 대한 것만 구현 받는 람다식을 제공
    System.out.println("exit");
  }

}
