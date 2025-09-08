package practice.reactiveprograming;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reactive Streams - Operators : 데이터 가공
 *
 * Publisher ---[data]---> Operator ----[data2]----> Operator2 ----[data3]---> SubScriber
 * 1. map (data1 -> f -> data2)
 *      : pub ----[data]---> mapPub (op) ----[data2]---> logSub   "downStream"
 *                                             <-- subscribe(logSub)
 *                                             --> onSubscribe(s)
 *                                             --> onNext
 *                                             --> onNext
 *                                             --> onComplete
 */
@Slf4j
public class PubSub {
    public static void main(String[] args) {
        // Pub : 데이터 제공자
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");

//        Publisher<Integer> map2Pub = mapPub(mapPub, s -> s / 5);
//        Publisher<Integer> sumPub = sumPub(pub); // 계산만 하고 있다가 계산 다 하면 그때야 보내는 pub
        Publisher<String> reducePub = reducePub(pub, "", (a, b) -> a+ "-" +b); // pub, 초기값, 계산로직
        // Sub : 데이터 받는자
        reducePub.subscribe(logSub());
    }


    // reduce 동작 과정 - 초기값 0 [1,2,3,4,5]  ---결과--> 1개
    // 0 -> (0,1) -> 0 + 1 = 1
    // 1 -> (1,2) -> 1 + 2 = 3
    // 3 -> (3,3) -> 3 + 3 = 6 ...
    private static Publisher<String> reducePub(Publisher<Integer> pub, String init, BiFunction<String, Integer, String> bf) {
        return new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> sub) {
                pub.subscribe(new DelegateSub<Integer, String>(sub) {
                    String result = init;
                    @Override
                    public void onNext(Integer i) {
                        result = bf.apply(result, i);
                    }
                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }


    // T -> R (sub)
    private static <T,R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T,R>(sub) {
                    @Override
                    public void onNext(T i) {
                        sub.onNext(f.apply(i));
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE); //  데이터 일단 이 정도 줘~ (백프레셔 역할 함)
                //s.cancel();   완료는 아니지만, pub에게 데이터 보내는걸 멈춰줘 할 때 쓴다. sub를 통해 취소를 할 수 있다 정도만 기억.
            }

            @Override
            public void onNext(T i) {
                log.info("onNext : {}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError : {}", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }

        };
    }

    private static <T> Publisher<T> iterPub(List<T> data) {
        return new Publisher<>() {
            // 제공할 데이터 예시

            @Override
            public void subscribe(Subscriber<? super T> sub) {
                sub.onSubscribe(new Subscription() { // Subscription : 구독 액션을 나타냄
                    @Override
                    public void request(long l) {
                        try {
                            data.forEach(s -> sub.onNext(s)); // 모든 원소를 순회하며 sub에 보낸다.
                            sub.onComplete(); // 꼭 끝났다고 말을 해줘야 함.
                        } catch (Throwable t) {
                            sub.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });

            }
        };
    }

}
