## 6. Subject

Observable 과 Observer 를 모두 구현한 추상 ㅏ입으로 하나의 소스로 부터 다중의 구독자에게 멀티 캐스팅이 가능. Observer를 구현하므로 `onNext()` `onError()` `onComplete()` 등의 메서드를 수동으로 호출하여 이벤트를 구독자들에게 전달할 수 있다.

<br>

#### PublishSubject

Subject를 구현한 가장 단순한 타입 중 한 가지로 구독자들에게 이벤트를 널리 전달한다. PublishSubject 객체를 생성하고 간단히 이벤트를 브로드캐스팅하는 예제를 살펴본다.

```kotlin
@Test
fun publishSubject() {
    val src: Subject<String> = PublishSubject.create()
    src.subscribe(
        { item: String -> println("A:$item") },
        { t: Throwable -> t.printStackTrace() }
    ) { println("A: onComplete") }
    src.subscribe(
        { item: String? -> println("B:$item") },
        { t: Throwable -> t.printStackTrace() }
    ) { println("B: onComplete") }
    src.onNext("Hello")
    src.onNext("World")
    src.onNext("!!!")
    src.onComplete()
    /*
    A:Hello
    B:Hello
    A:World
    B:World
    A:!!!
    B:!!!
    A: onComplete
    B: onComplete
     */
}
```

`create()` 메서드를 통해 간단히 Subject 객체를 생성. 

Subejct는 Observable 이면서 Observer이므로 발행과 구독을 모두 Subject 객체를 통해 하는 것을 확인할 수 있음

<br>

Subject를 이용할 때 주의해야 할 점은 Subject는 Hot Observable 이라는 사실을 잊지 말아야 한다는 것.

다음과 같이 아이템을 발행한 뒤 구독하면 아무런 아이템도 소비할 수 없음

```kotlin
@Test
fun hotObservable() {
    val src : Subject<String> = PublishSubject.create()
    src.onNext("Hello")
    src.onNext("World")
    src.onNext("!!!")
    src.onComplete()
    src.map(String::length)
        .subscribe(System.out::println)
}
```

<br>

Subject는 Observer 이기도 하므로, 다른 Observable의 구독자로 이벤트를 처리할 수도 있다.

소비하는 아이템은 다시 Observable로 발행하여 다른 구독자에게 전달함.

```kotlin
@Test
fun subjectExample() {
    val src1 = Observable.interval(1, TimeUnit.SECONDS)
    val src2 = Observable.interval(500, TimeUnit.MILLISECONDS)
    val subject = PublishSubject.create<Any>()

    src1.map { item -> "A:$item" }.subscribe(subject)
    src2.map { item -> "B:$item" }.subscribe(subject)
    subject.subscribe(System.out::println)
    Thread.sleep(5000)
}
```

Subject를 통해 구독하고 아이템을 재발행도 하고, merge 연산자처럼 두 Observable 소스를 묶어서 이벤트를 관리하는 것도 가능하다는 것을 확인 할 수 있음

<br>

#### SerializedSubject

SerializedSubject는 사실 접근 제어자가 public이 아니므로 RxJava 내부에서만 접근 가능한 타입이다. 만약 서로 다른 스레드에서 Subject에 접근하여 아이템을 발행하는 상황에서는 Subject가 스레드 안전을 보장하지 않음. 애플리케이션에서 이 타입에 접근은 불가능하지만 사용은 할 수 있음

<br>

두 개의 스레드가 동시에 메모리에 접근하다 보니 이를 통과하는 경우가 생겨 결국 임의로 만든 IllegalArgumentException 이 발생 할 수 있다. 이처럼 스레드에 안전하지 않은 경우를 해결하려면 Subject.toSerialized() 메서드를 통해 SerializedSubject를 객체로 생성할 수 있다. SerializedSubject는 내부에서 synchronized 키워드를 통해 스레드를 제어해 스레드에 안전한 Subject를 제공한다. 

<br>

#### BehaviorSubject

또 다른 Subject의 서브 클래스로 BehaviorSubject 가 있다. BehaviorSubject는 PublishSubject 와 동일하게 동작하지만, 차이점은 새로운 Observer를 통해 구독 시 가장 마지막 아이템만을 발행한다는 것이다. 가장 최근 상태값을 가져오는 것이 중요 할 때 사용 할 수 있다.

구독을 시작할 때 Subject가 마지막으로 발행한 아이템을 가져오며, 이후에 발행되는 아이템들은 PublishSubject와 동일하게 모두 수신할 수 이있다.

<br>

#### ReplaySubject

ReplaySubject 는 PublishSubject에 cache 연산자를 적용한 것과 유사하다. ReplaySubject는 새로운 구독자가 구독을 요청하면 이전에 발행했던 아이템 모두를 구독자에게 전달한다.

매개 변수가 없는 replay 연산자 또는 cache 연산자와 매우 비슷하다. ReplaySubject를 사용할 때는 큰 볼륨을 갖는 아이템 발행 또는 무한한 아이템을 발행하는 소스에 대해서는 고민을 해보아야 한다. > 그렇지 않으면 메모리가 가득 차 OutOfMemoryException 이 발생할 수 있다.

<br>

#### AsyncSubject

AsyncSubject는 `onComplete()` 호출 직전에 발행된 아이템만을 구독자들에게 전달하는 특징이 있다.

<br>

#### UnicastSubject

UnicastSubject는 다른 Subject 처럼 아이템을 발행하고 구독한다. 차이점은 Observer가 UnicastSubject에 구독하기 전 까지는 발행하는 아이템을 버퍼에 저장하고, 구독이 시작될 때 버퍼에 있던 아이템을 모두 발행하고 버퍼를 비워낸다. 그렇기에 구독자를 여러 개 둘 수가 없다. 첫 번째 구독자가 모든 아이템을 다 소비해 두번째 구독자부터는 아이템을 수신할 수 없기 때문이다. 두 번째 구독을 시도한다면 IllegalStateException 예외를 던지며 하나의 옵서버만 허용한다는 메시지를 볼 수 있다.

```kotlin
@Test
fun unicastSubjectExample() {
    val subject: Subject<Long> = UnicastSubject.create()
    Observable.interval(1, TimeUnit.SECONDS)
        .subscribe(subject)
    Thread.sleep(3000)
    subject.subscribe { i -> println("A : $i") }
    Thread.sleep(2000)
    /* Result
    A : 0
    A : 1
    A : 2
    A : 3
    A : 4
     */
}
```

첫 3초 동안은 발행된 아이템을 UnicastSubject의 버퍼에 축적해 콘솔에 아무런 출력이 없다가 3초 이후 축적된 아이템을 모두 방출하고 그 이후로 2초 동안은 1초마다 발행 된 아이템이 콘솔에 출력되는 것을 확인할 수 있음

<br>

---

