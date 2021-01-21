## 5. 배압과 Flowable

### <br>

### **배압(Backpressure)**

RxJava 에서 Observable은 생산자와 소비자로 나눌 수 있는데 소비자의 소비량과 관계없이 생산자가 아이템을 계속해서 발행하여 발행과 소비가 균형적으로 일어나지 않는 현상

배압을 제어하지 못하면 OutOfMemoryError Exception 을 포함한 많은 문제를 발생시킬 수 있다.

<br>

<br>

### Flowable 다루기

배압을 직접 제어할 수도 있지만, RxJava에서는 스트림에 쌓이는 아이템의 양을 제어할 수 있는 솔루션을 제공한다.

```kotlin
Flowable.range(1, Int.MAX_VALUE).map { item ->
    println("item 발행 : $item")
    item
}.observeOn(Schedulers.io())
    .subscribe { item ->
        sleep(100)
        println("item 소비 : $item")
    }
sleep(30 * 1000)
```

해당 코드의 결과를 보면 아이템 발행량이 일정량 누적되면 더는 아이템을 발행하지 않는 것을 확인할 수 있다.

배압을 스스로 조절하는 점이 Flowable 과 Observable 의 차이점

발행과 소비의 차이를 코드 결과에서 확인 가능한데 발행한 것을 모두 소비하지 않는 결과를 확인 할 수있는데 

이는 다시 생산자가 발행하기까지 걸리는 시간으로 인해 소비자가 기다리는 일이 없도록 여유를 두기 위함이다.

<br>

하지만 시간을 기반으로 하는 interval 연산자와 Flowable 을 같이 사용하면 문제가 발생할 수 있음

interval과 같은 연산자들은 스케줄러의 설정과 관계없이 시간을 기반으로 충실히 아이템을 발행

그러므로 생산하는 쪽에서 블로킹 이슈가 발생하면 배압 제어 전략과 관계없이 `MissingBackPressureException` 이 발생

이러한 예외 상황이 발생할 수 있다는 것을 충분히 숙지하고 Flowable을 사용해야함

<br>

<br>

### **[배압 제어 연산자](http://reactivex.io/documentation/operators/backpressure.html)**

이미 만들어진 Flowable에 대해 배압에 대한 전략을 설정할 수 있도록 도와주는 배압 제어 연산자를 적용할 수 있음

배압 제어 연산자를 적용시 interval 연산자를 같이 사용할 때 발생할 수 있는 `MissingBackPressureException` 을 극복할 수 있음

<br>

- **onBackpressureBuffer**
  - 배압이 구현 되지 않은 Flowable 에 대해 BackpressureStrategy.BUFFER 를 적용.
  - 매개 변수별로 종류가 많은데 용량, 지연, 오버플로 콜백 등에 대한 것을 정의할 수 있음

<br>

- **onBackpressureLatest**
  - 스트림 버퍼가 가득 차면 최신의 아이템을 버퍼에 유지하려고 오래된 아이템을 버리는 연산자
  - 최신의 상태나 데이터만 의미가 있을 때 사용하면 좋음

<br>

- **onBackpressureDrop**
  - 버퍼가 가득 찬 상태에서 버퍼에 든 아이템을 소비하는 쪽이 바쁘다면 발행된 아이템을 버림

<br>

<br>

### **Flowable 생성과 배압 전략**

`Flowable.create()` 는 `Observable.create()` 과 비슷하다.

EmitterBackpressure Stragtegy(배압 전략) 을 설정해야함

배압 전략은 발행된 아이템들의 캐싱 및 폐기 여부를 지정하거나 아니면 배압 제어를 구현하지 않도록 설정할 수 있음

```kotlin
Flowable.create(
    FlowableOnSubscribe { emitter: FlowableEmitter<Int?> ->
        for (i in 0..1000) {
            if (emitter.isCancelled) {
                return@FlowableOnSubscribe
            }
            emitter.onNext(i)
        }
        emitter.onComplete()
    }, BackpressureStrategy.BUFFER
)
    .subscribeOn(Schedulers.computation())
    .observeOn(Schedulers.io())
    .subscribe((System.out::println), { throwable -> throwable.printStackTrace() })
sleep(100)
```

<br>

배압 전략에는 다음과 같은 것들이 존재

- **BackpressureStrategy.MISSING**
  - 기본적으로 배압 전략을 구현하지 않음
  - 오버플로를 다루려면 배압 제어 연산자를 사용해야함
- **BackpressureStrategy.ERROR**
  - 스트림에서 소비자가 생산자를 따라가지 못하는 경우  `MissingBackPressureException` 예외를 발생 시킴
- **BackpressureStrategy.BUFFER**
  - 구독자가 아이템을 소비할 때까지 발행한 아이템들을 버퍼에 넣어둠
  - 제한이 없는 큐지만, 가용 메모리를 벗어나는 경우 `OutOfMemoryError` 를 발생시킬 수 있으므로 유의해야함
- **BackpressureStrategy.DROP**
  - 구독자가 아이템을 소비하느라 바빠서 생산자를 못 따라가는 경우 발행된 아이템을 모두 무시하고 버림
- **BackpressureStrategy.LATEST**
  - 구독자가 아이템을 받을 준비가 될 때까지 가장 최신의 발행된 아이템들만 유지하고 이전 아이템은 버림

<br>

---

<br>

<br>