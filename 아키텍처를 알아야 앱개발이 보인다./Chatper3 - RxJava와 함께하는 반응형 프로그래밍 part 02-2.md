## 2.  Observable(2)

<br>

### 다양한 Observable 의 형태

[Obervable(1)](https://github.com/JJJoonngg/android-books/blob/main/%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98%EB%A5%BC%20%EC%95%8C%EC%95%84%EC%95%BC%20%EC%95%B1%EA%B0%9C%EB%B0%9C%EC%9D%B4%20%EB%B3%B4%EC%9D%B8%EB%8B%A4./Chatper3%20-%20RxJava%EC%99%80%20%ED%95%A8%EA%BB%98%ED%95%98%EB%8A%94%20%EB%B0%98%EC%9D%91%ED%98%95%20%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D%20part%2002-1.md) 이외에 조금은 특별한 스트림들이 존재한다.

- Single
- Maybe
- Completable

이들을 초기화하고 연산자를 이용하는 방법은 거의 동일하다.

<br>

<br>

#### [**Single**](http://reactivex.io/documentation/single.html)

Single은 Obervable과 다르게 단 하나의 아이템만을 발행하는 특징이 존재

그러므로 `just()` 연산자에는 하나의 인자만을 취할 수 있음.

```kotlin
Single.just("Hello World")
    .subscribe(System.out::println)
```

<br>

`create()` 연산자를 사용하는 경우 Emitter를 사용하여 데이터를 발행. 

데이터를 단 한 번만 발행하므로 `onNext()` 와 `onComplete()` 메서드를 호출하는 대신 `onSuccess(T)` 로 두 메서드를 대체

```kotlin
Single.create { emitter: SingleEmitter<Any?> -> emitter.onSuccess("Hello") }
    .subscribe(System.out::println)
```

<br>

오류를 다루는 경우 Observable의 Emitter와 동일하게 `onError()` 를 호출하여 오류를 구독자들에게 통지할 수 있음

<br>

몇 가지의 RxJava 연산자들은  Observable을 Single로 변환시키곤함

```kotlin
val src = Observable.just(1, 2, 3)
val singleSrc1 = src.all { i: Int -> i > 0 }
val singleSrc2 = src.first(-1)
val singleSrc3 = src.toList()
```

<br>

또한 Single도 필요에 따라 Observable로 변환해야 하는 경우가 있다. 그런 경우 `toObservable() ` 연산자를 사용할 수 있다.

```kotlin
val singleSrc = Single.just("Hello World")
val observableSrc = singleSrc.toObservable()
```

모든 소스의 경우에 Observable로 변환하는 것뿐만 아니라 to~ 연산자를 이용하면 다른 소스 형태로 바꾸는 것이 가능

<br>

Single은 단일 아이템을 발행한다는 점에서 HTTP request/response 와 같은 이벤트를 처리하는 경우 자주 사용된다.

<br>

<br>

#### **Maybe**

Maybe는 Single과는 비슷하지만 아이템을 발행하거나 발행하지 않을 수도 있다는 점에서 차이가 존재함.

아이템을 발행할 때는 `onSuccess(T)` 를 호출하고, 발행하지 않을 때는 `onComplete()` 를 호출.

그러므로 `onSuccess(T)` 를 호출하는 경우 `onComplete()` 를 호출할 필요는 없음.

```kotlin
Maybe.create { emitter: MaybeEmitter<Any?> ->
    emitter.onSuccess(100)
    emitter.onComplete() // 무시됨
}.doOnSuccess { item: Any? -> println("doOnSuccess1") }
    .doOnComplete { println("doOnComplete1") }
    .subscribe(System.out::println)

Maybe.create { emitter: MaybeEmitter<Any?> ->
    emitter.onComplete()
}.doOnSuccess { item: Any? -> println("doOnSuccess2") }
    .doOnComplete { println("doOnComplete2") }
    .subscribe(System.out::println)
/*결과
doOnSuccess1
100
doOnComplete2
*/
```

<br>

몇 가지 Observable 연산자는 반환 타입을 Maybe로 변환한다.

```kotlin
val src1 = Observable.just(1, 2, 3)
val srcMaybe1 = src1.firstElement()
srcMaybe1.subscribe(System.out::println)

val src2 = Observable.empty<Int>()
val srcMaybe2 = src2.firstElement()
srcMaybe2.subscribe(
    { x: Int? -> println(x) },
    { throwable: Throwable? -> })
{ println("onComplete!") }
/*결과
1
onComplete!
*/
```

<br>

<br>

#### **Completable**

Completable은 아이템을 발행하지 않고, 단지 정상적으로 실행이 종료되었는지에 대해 관심을 가짐

Emitter에서 `onNext()` 나 `onSuccess()` 같은 메서드는 없고 `onComplete()`와 `onError()` 만 존재

```kotlin
Completable.create { emitter ->
    //do something here
    emitter.onComplete()
}.subscribe { println("completed1") }

Completable.fromRunnable {
    //do something here
}.subscribe { println("completed2") }
/*
결과
completed1
completed2
*/
```

<br>

<br>

### Cold Observable vs Hot Observable

- Cold Observable
  - Observable에 구독을 요청하면 발행하기 시작.
  - 아이템은 처음 부터 끝까지 발행되고, 임의로 종료시키지 않는 이상 여러 번 요청에도 처음부터 끝까지 발행 하는 것을 보장

```kotlin
@Test
fun coldObservable() {
    Observable.interval(1, TimeUnit.SECONDS).also {
        it.subscribe { value -> println("#1 : $value") }
        Thread.sleep(3000)
        it.subscribe { value -> println("#2 : $value") }
        Thread.sleep(3000)
    }

    /*
    결과
    #1 : 0
    #1 : 1
    #1 : 2
    #1 : 3
    #2 : 0
    #1 : 4
    #2 : 1
    #1 : 5
    #2 : 2
     */
}
```

Observable을 구독하고 3초 뒤에 새로운 구독자로 다시 구독 했을 때도 처음부터 다시 아이템을 발행하는 것을 확인 할 수 있음

<br>

- Hot Observable
  - 아이템 발행이 시작된 이후로 모든 구독자에게 동시에 같은 아이템을 발행
  - 브로드 캐스트 메시지를 글로벌하게 전송하는것 과 비슷
  - 두개의 구독자가 같은 하나의 Observable 을 구독시, 두 구독자는 같은 아이템을 수신하지만  어느 하나의 구독자는 구독하기 전에 발행된 아이템을 놓칠 수 있다.

<br>

#### **publish 연산자와 connect 연산자**

ConnectableObservable은 Hot Observable을 구현할 수 있도록 도와주는 타입

아무런 Observable 타입이나 publish 연산자를 이용하여 간단히 Connectable Observable로 변환 가능

ConnectableObservable은 구독을 요청해도 Observable은 데이터를 발행하지 않음

- `connect()` 연산자를 호출할 때 비로소 아이템을 발행하기 시작

```kotlin
@Test
fun connectableObservable() {
    Observable.interval(1, TimeUnit.SECONDS).publish().also {
        it.connect()
        it.subscribe { value -> println("#1 : $value") }
        Thread.sleep(3000)
        it.subscribe { value -> println("#2 : $value") }
        Thread.sleep(3000)
    }
    /*
    결과
    #1 : 0
    #1 : 1
    #1 : 2
    #1 : 3
    #2 : 3
    #1 : 4
    #2 : 4
    #1 : 5
    #2 : 5
     */
}
```

첫 번째 구독 시에 3초동안 0~2 발행하고, 3초뒤에는 두번째 구독자가 추가되었지만 0~2는 수신하지 못하고 3부터 수신하는 것을 확인할 수 있음

<br>

#### **autoConnect 연산자**

connect 연산자를 호출하지 않더라도, 구독시에 즉각 아이템을 발행할 수 있도록 도와주는 연산자

매개 변수는 아이템을 발행하는 구독자 수로 내부의 숫자 이상 붙어야 아이템을 발행하기 시작

매개 변수로 0이하를 입력시 구독자 수와 관계없이 곧바로 아이템을 발행하기 시작

매개 변수를 지정하지 않으면 `autoConnect(1)` 와 동일하게 동작, 구독하자마자 아이템을 발행하기 시작

```kotlin
@Test
fun autoConnect() {
    Observable.interval(100, TimeUnit.MILLISECONDS).publish().autoConnect(2).also {
        it.subscribe { value -> println("A : $value") }
        it.subscribe { value -> println("B : $value") }
        Thread.sleep(500)
    }
    /*
    결과
    A : 0
    B : 0
    A : 1
    B : 1
    A : 2
    B : 2
    A : 3
    B : 3
    A : 4
    B : 4
     */
}
```

<br>

#### **Disposable 다루기**

subscribe() 메서드를 호출함면 Disposable 객체를 반환

유한한 아이템을 발행하는 Observable의 경우 `onComplete()` 의 호출로 안전하게 종료

무한하게 아이템을 발행하거나 오랫동안 실행되는 경우 구독이 필요하지 않은 경우가 생길 시 메모리 누수 방지를 위해 명시적인 폐기(dispose)가 필요

`Disposable.dispose()` 메서드 호출시 언제든지 아이템 발행 중단 가능

Observable을 `dispose()` 하면 아이템의 발행이 중지되고 모든 리소스가 폐기됨

리소스가 폐기되었는지 확인하는 데 `Disposable.dispose()` 메서드를 활용할 수 있으며, `dispose()` 내부에서 폐기 여부를 체크하므로 `isDispose()` 의 결과를 확인하고 `dispose()` 를 호출할 필요는 없음

`onComplete()` 를 명시적으로 호출하거나 호출됨이 보장된다면 `dispose()` 를 호출할 필요는 없음

<br>

#### **CompositeDisposable**

여러 곳에 있는 구독자를 한꺼번에 폐기할 수 있다.

```kotlin
@Test
fun compositeDisposable() {
    val source = Observable.interval(1000, TimeUnit.MILLISECONDS)

    val d1 = source.subscribe(System.out::println)
    val d2 = source.subscribe(System.out::println)
    val d3 = source.subscribe(System.out::println)
    val cd = CompositeDisposable()
    cd.addAll(d1,d2,d3)
}
```

---





