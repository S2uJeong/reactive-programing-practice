# To-do
[1:10:00](https://www.youtube.com/watch?v=Wlqu1xvZCak&list=PLOLeoJ50I1kkqC4FuEztT__3xKSfR2fpw&index=3)

- 비동기 
  - 어떤게 먼저 올지 모르는데 순서 어케 정해 ? 
  - 하나의 퍼블리셔는 싱글쓰레드를 사용하기 때문에 넘어가는 순서가 뒤바뀌지 않고 큐에 쌓였다가 실행된다. 
- push 방식이라 데이터가 계속 들어올텐데 멈추는 기준을 어케 정해 ? 
# Reactive Programing
## 참고 자료
- https://projectreactor.io/docs
- https://github.com/reactive-streams/reactive-streams-jvm

## Operators
- PubSub.java


## Others
### 아래 코드에서 Throwable  대신 Exception을 넣는 것의 차이

```java
@Override
public void subscribe(Subscriber<? super Integer> sub) { // Sub : 데이터 받는자
    sub.onSubscribe(new Subscription() { // Subscription : 구독 액션을 나타냄
        @Override
        public void request(long l) {
            try {
                iter.forEach(s -> sub.onNext(s)); // 모든 원소를 순회하며 sub에 보낸다.
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
```

**Reactive Streams 구현체에서는 `Throwable`이 더 적절합니다:**

1. **완전한 오류 전파**: Reactive Streams 명세에서는 <u>모든 종류의 오류를 downstream으로 전파</u>하도록 권장
2. **일관성**: RxJava, Project Reactor 등 주요 구현체들이 `Throwable`을 사용
3. **예상치 못한 오류 처리**: `<u>Error`가 발생해도 스트림이 중단되지 않고 적절히 처리</u>됨

**일반적인 애플리케이션 코드에서는 `Exception`이 더 적절:**

- `Error`는 보통 복구 불가능하므로 잡지 않는 것이 좋음
- 애플리케이션 레벨의 예외만 처리하는 것이 명확함

따라서 현재 코드에서 `Throwable`을 사용하는 것이 Reactive Streams 패턴에 더 적합합니다.
