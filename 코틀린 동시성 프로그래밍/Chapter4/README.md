# Chapter 4. 일시 중단 함수와 코루틴 컨텍스트



## suspend function (일시 중단 함수)

```kotlin
suspend fun greetDelayed(delayMillis: Long){
  delay(delayMillis)
  println("Hello, World!")
}
```

<br>

### 방법

함수의 시그니처에 `suspend` 제어자만 추가하면된다.

> 비 일시 중단 함수에서 (non-suspending) 에서 호출시 코루틴 빌더로 감싸야함

```kotlin
...
runBolocking{
  greetDelayed(1000)
}
...
```

<br><br><br>

### 장점

비동기 구현에 비해 `await()` 와 같은 호출을 할필요가 없다.

인터페이스 구현시에 상세 구현 내용은 노출 할 필요가 없다.

>비동기 interface 및 호출

```kotlin
interface TestRepository{
  fun asyncFetchTest(test:String) : Deferred<Data>
}
///
fun main() = runBlocking{
  val client : TestRepository = TestRepository()
  val test = client.asyncFetchTest("test").await()
}
```

<br>

> 일시 중단 함수 interface 및 호출

```kotlin
interface TestRepository{
  suspend fun fetchTest(test:String) : Data
}
///

class Client:TestRepository{
  override suspend fun fetchTest(test:String):Data{
    return Data(test, "TestData")
  }
}
```

<br><br><br>



## Coroutine Context

코루틴은 항상 context 안에서 실행된며, context는 코루틴이 어떻게 실행되고 동작해야 하는지를 정의할 수 있게 해주는 요소들의 그룹

<br>

### [Dispatcher](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/index.html)

코루틴이 실행될 스레드를 결정, 시작될 곳과 중단 후 재개될 곳을 모두 포함

- Default
  - 따로 context가 지정되지 않은 경우, launch, async 등과 같은 모든 표준 빌더에서 사용하는 기본
- IO
  - 공유 스레드 풀에서 IO 작업을 실행하는데 최적화된 디스패처
- Main
  - Android 메인 스레드에서 코루틴을 실행하는 디스패처 (UI 상호작용 작업용)

- Unconfined
  - 첫 번째 중단 지점에 도달할 떄 까지 현재 스레드에 있는 코루틴을 실행
  - 코루틴은 일시 중지 후에, 일시 중단 연산에서 사용된 기존 스레드에서 다시 시작됨

<br>

<br>

### **예외 처리**

코루틴 컨텍스트의 용도중 하나인 예측이 어려운 예외에 대한 동작을 정의

user가 handler하지 않는 exception 들에 대해서만 작동

`CoroutineExceptionHandler` 를 구현해서 만들 수 있음

> JVM 에서는 모든 coroutine에 대한 global exception handler를 재정의 가능

```kotlin
val handler = CoroutineExceptionHandler({ context, thorwable->
     println("Error captured in $context")   
     println("Message : ${throwable.message}")
})
```

<br>

<br>

### **컨텍스트에 대한 추가 정보**

<br>

#### **결합**

더하기 연산자를 사용해서 코루틴을 결합하여 사용할 수 있다.

```kotlin
val exceptionHandler = CoroutineExceptionHandler({ context, thorwable->
     println("Error captured in $context")   
     println("Message : ${throwable.message}")
})


CoroutineScope(Dispatchers.Default + exceptionHandler){
  println("Running in ${Thread.currentThread().name}")
}
```

<br>

#### **분리**

결합된 컨텍스에서 컨텍스트 요소를 제거할 수 있으며 제거를 위해선 제거할 요소의 키에 대한 참조가 있어야한다.

```kotlin
...
//결합
val context = dispatcher + handler

//하나의 요소 제거
val tmpCtx = context.minusKey(dispatcher.key)
...
```

<br>

#### **witchContext를 사용하는 임시 컨텍스트 스위치**

이미 일시 중단 함수 상태에 있을 떄 `withContext()` 를 사용해 코드 블록에 대한 컨텍스트를 변경할 수 있다.

```kotlin
Coroutine(Dispatcher.Default){
  witchContext(Dispatcher.Main){
    //UI Process
  }
}
```

<br>

<br>

<br>

---

