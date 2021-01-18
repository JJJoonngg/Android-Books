## 4. 스케줄러

멀티 스레드와 같은 비동기 작업을 돕는 도구.

Schedulers 클래스에서 제공하는 정적 팩토리 메서드를 통해 스케줄러를 설정할 수 있음

```kotlin
val io = Schedulers.io()
val computation = Schedulers.computation()
val trampoline = Schedulers.trampoline()
val newThread = Schedulers.newThread()
//For Android
val mainThread = AndroidSchedulers.mainThread()
```

<br><br>

### 스케줄러의 종류

- **IO 스케줄러** : 네트워크, DB, File System 환경 등의 블로킹 이슈가 발생하는 곳에서 비동기 적인 작업을 위해 사용
- newThread 스케줄러 : 매번 새로운 스케줄러(스레드)를 생성
- **Computation 스케줄러** : 단순 반복적인 작업, 콜백 처리 긜고 다른 계산적인 작업에 사용. 블로킹 이슈가 발생하는 곳에서 사용하는 것을 추천하지 않음
- **Trampoline 스케줄러** : 새로운 스레드를 생성하지 않고 현재 스레드에 무한한 크기의 큐를 생성하는 스케줄러. 모든 작업을 순차적으로 실행하는 것을 보장(FIFO)
- **mainThread 스케줄러** : RxAndroid 에서는 안드로이드 메인 스레드에서 작동하는 스케줄러를 제공

<br>

<br>

### subscribeOn 과 observeOn 연산자

스케줄러를 이용하는 방법으로 subscribeOn 과 observeOn 연산자를 제공.  

이 연산자들을 이용하여 간단히 멀티 스레딩을 구현할 수 있음

```kotlin
@Test
fun subscribeAndObserveOn() {
    Observable.create<Int> { emitter ->
        for (i in 0..2) {
            val threadName = Thread.currentThread().name
            println("#Subs on $threadName $i")
            emitter.onNext(i)
            Thread.sleep(100)
        }
        emitter.onComplete()
    }.apply {
        subscribe{s->
            val threadName = Thread.currentThread().name
            println("#Obsv on $threadName $s")
        }
    }
    /* Result
    #Subs on main 0
    #Obsv on main 0
    #Subs on main 1
    #Obsv on main 1
    #Subs on main 2
    #Obsv on main 2
     */

}
```

스레드이름을 출력하도록 했고, 모두 메인스레드에서 동작하는 것을 확인

<br>

`subscribeOn` 연산자를 사용해 스레드를 지정

```kotlin
Observable.create<Int> { emitter ->
            for (i in 0..2) {
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName $i")
                emitter.onNext(i)
                Thread.sleep(100)
            }
            emitter.onComplete()
        }.apply {
            subscribeOn(Schedulers.io()).subscribe() { s ->
                val threadName = Thread.currentThread().name
                println("#Obsv on $threadName $s")
            }
            Thread.sleep(500)
        }
        /* Result
        #Subs on RxCachedThreadScheduler-1 0
        #Obsv on RxCachedThreadScheduler-1 0
        #Subs on RxCachedThreadScheduler-1 1
        #Obsv on RxCachedThreadScheduler-1 1
        #Subs on RxCachedThreadScheduler-1 2
        #Obsv on RxCachedThreadScheduler-1 2
         */
```

`subscribeOn` 연산자는 Observable 소스에 어떤 스케줄러를 사용하여 아이템을 발행할지 알려줌

만약 `subscribeOn` 연산자만 있고, `observeOn` 이 없다면 해당 스케줄러는 아이템 발행 및 구독까지 Observable 체인 전체에 작용

즉 위 예제 실행 결과처럼 메인 스레드를 사용하지 않고 발행, 구독 모든 부분에서 스케줄러의 스레드를 사용함

<br>

```kotlin
Observable.create<Int> { emitter ->
    for (i in 0..2) {
        val threadName = Thread.currentThread().name
        println("#Subs on $threadName $i")
        emitter.onNext(i)
        Thread.sleep(100)
    }
    emitter.onComplete()
}.apply {
    observeOn(Schedulers.computation())
        .subscribeOn(Schedulers.io())
        .subscribe() { s ->
            val threadName = Thread.currentThread().name
            println("#Obsv on $threadName $s")
        }
    Thread.sleep(500)
}

/* Result
#Subs on RxCachedThreadScheduler-1 0
#Obsv on RxComputationThreadPool-1 0
#Subs on RxCachedThreadScheduler-1 1
#Obsv on RxComputationThreadPool-1 1
#Subs on RxCachedThreadScheduler-1 2
#Obsv on RxComputationThreadPool-1 2
 */
```

`observeOn` 연산자를 사용하여 스케줄러를 지정하면 Observable 에서 발행된 아이템을 가로채어 해당 스케줄러로 아이템을 구독

따라서 위의 코드의 해당 경우에 따라 스레드의 이름이 다른 것을 확인 가능

<br>

`interval` `timer` `replay` `buffer` 등의 연산자는 computation 스케줄러로 이미 고정되어 다른 스케줄러를 지정하더라도 무시됨

```kotlin
Observable.interval(200, TimeUnit.MILLISECONDS)
    .subscribeOn(Schedulers.io())
    .subscribe { value -> println("${Thread.currentThread().name} : $value") }
Thread.sleep(1000)
/* Result
RxComputationThreadPool-2 : 0
RxComputationThreadPool-2 : 1
RxComputationThreadPool-2 : 2
RxComputationThreadPool-2 : 3
RxComputationThreadPool-2 : 4
 */
```

<br>

<br>

일반적으로 android application 개발 시에는 네트워크나 데이터 베이스로 부터 데이터를 요청해야하는 경우, 메인 스레드가 블로킹 되는 것을 방지하도록 IO 스케줄러를 사용

요청한 데이터를 구독한 결과로 UI 를 갱신해야하고, UI 는 메인 스레드에서만 갱신이 가능하므로 mainThread 스케줄러를 다음과 같이 사용

```kotlin
repository.getUsers()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe{ users->
					//UI UPDATE				
				}
```





---

