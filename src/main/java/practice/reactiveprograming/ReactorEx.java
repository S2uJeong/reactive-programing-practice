package practice.reactiveprograming;

import reactor.core.publisher.Flux;

/**
 * Publisher의 일종
 */
public class ReactorEx {

  public static void main(String[] args) {
    Flux.<Integer>create(sink -> {
          sink.next(1);
          sink.next(2);
          sink.next(3);
          sink.complete();
    })
        .log()
        .map(s -> s*10)
        .log() // 데이터가 흐르는 과정을 찍어줌.
        .subscribe(sub -> System.out.println(sub));
  }
}
