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



---

