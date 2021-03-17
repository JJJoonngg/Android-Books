## 2.Lifecycles

<br>

### 생명 주기를 인식하는 컴포넌트 다루기

생명 주기 인식 (Lifecycle-aware) 컴포넌트는 액티비티 또는 프래그먼트 같은 다른 컴포넌트의 생명 주기 상태가 변경 될 떄 이에 대응하는 라이브러리

더욱 체계적으로 구성하고, 가벼운 코드를 유지 보수하기 쉬움

<br>

androidx.lifecycle 패키지는 생명 주기 상태에 따라 자동으로 동작을 조정할 수 있는 클래스와 인터페이스를 제공

<br>

안드로이드 프레임워크의 대부분의 컴포넌트가 생명 주기를 가짐. 생명 주기는 운영 체제 또는 프로세스 내 실행 중인 프레임워크 코드에 의해 관리됨.

생명주기는 안드로이드가 동작하는 방식의 핵심이 되는 부분이고, 개발자는 이를 반드시 고려해야함 . 그렇지 않으면 메모리 누수가 발생하거나 크래시가 발생할 수 있음

<br>

`onStart()` `onStop()` 과 같은 생명 주기 메서드에 많은 양의 코드를 처리하는 것은 부담이 될 수 있으며,

`onStart()` 에서 시작한 작업이 끝나기전에 `onStop()` 이 호출되면 상황에 따라 문제가 발생할 수 있음

androidx.lifecycle 패키지는 이러한 문제를 유연하게 격리된 방식으로 해결할 수 있는 인터페이스와 클래스를 제공

<br>

<br>

### Lifecycle 클래스

컴포넌트의 생명 주기 상태에 대한 정보를 가지고 다른 객체가 이를 관찰할 수 있도록 돕는 클래스

이벤트와 상태라는 두가지 주요 사항을 통해 연관된 컴포넌트들의 생명 주기 상태를 추적

그래프로 따지면 상태는 노드이고, 이벤트는 두개의 노드 사이를 이동하는 사건

<br>

#### **이벤트(Event)**

프레임워크와 Lifecycle 클래스로부터 얻는 생명 주기 이벤트를 말함

이러한 이벤트 들은 액티비티와 프래그먼트의 콜백 이벤트에 매핑됨

<br>

#### **상태(State)**

Lifecycle 객체가 추적한 컴포넌트의 현재 상태를 뜻함

<br>

일반 클래스에 LifecycleObserver 인터페이스를 구현하고, `@OnLifecycleEvent` 애노테이션을 붙인다. 

이 클래스를 Lifecycle 객체의 `addObserver()` 메서드를 통해 넘김으로써 컴포넌트의 생명 주기 변화를 감지 할 수 있음

```kotlin
class MyObserver : LifecycleObserver{
  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun connectListener(){
    ...
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  fun disconnectListener(){
    ...
  }
}

lifecycleOwner.lifecycle.addObserver(MyObserver())
```

<br>

<br>

### LifecycleOwner

Lifecycle의 소유권을 추상화하는 인터페이스로 Lifecycle 을 반환하는 `getLifecycle()` 메서드 하나만을 가짐

서포트 라이브러리에 포함된 AppcompatActivity , Fragment 는 LifecycleOwner 가 이미 구현 되어 편하게 사용 가능하며, 

구현되지 않은 독립적인 클래스들은 LifecycleOwner를 구현하여 다른 코드들이 자체적인 생명 주기에 반응하여 작동하도록 할 수 있음

<br>

LifecycleOwner 의생명 주기를 관찰하기 위해선 LifecycleObserver를 구현해야함

생명 주기 정보를 제공하고 등록된 LifecycleObserver는 생명 주기 변화를 관찰 함

> 상위 예제에서 LifecycleObserver를 구현하고, Listener가 스스로 생명주기를 관리할 수 있도록 한 예제

```kotlin
class MyActivity : AppCompateActivity{
	prievate lateinit var myLocationListener : MyLocationListener
  
  @Override
  fun onCreate(...){
    ...
    myLocationListener = MyLocationListener(
      this, lifecycle, loaction ->{
        // update UI
      }
    )
    
    Util.checkUserStatus(){result ->
       if(result){
         myLocationListener.enable()
       }
    }
  }
}
```

일반적인 경우에서 Lifecycle 의 상태가 적절하지 않으면 콜백을 호출 하는 것은 피해야함

예시로 액티비티 상태가 저장된 후 콜백에서 프래그먼트 트랜잭션을 수행시, 크래시가 발생할 수 있으므로 이런 경우 콜백을 호출하지 말아야함

<br>

이런 Use case 를 쉽게 풀도록 Lifecycle 은 현 상태를 가져올 수 있는 메서드를 제공함

```kotlin
class MyLocationListener : LifecycleObserver{
  private var enabled = false
  private lateinit var lifecycle : Lifecycle
  private lateinit var context : Context
  
  MyLocationListener(context:Context, lifecycle:Lifecycle, callback : Callback){
    ...
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun start(){
    if(enabled){
      // connect service
    }
  }
  
  fun enable(){
    enabled = true
    
    if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
      //현재 상태를 쿼리
      //서비스가 연결되지 않았다면 연결
    }
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  fun stop(){
    // disconnect service
  }
}
```

만약 다른 컴포넌트에서 사용시 단지 초기화만 해주면됨. 모든 설정과 해제는 클래스 내부에서 직접 관리

<br>

구글에서는 ViewModel 또는 LiveData 와 같은 생명 주기를 아는 컴포넌트를 사용하여 애플리케이션을 개발하는 것을 추천

이러한 컴포넌트들은 생명 주기 문제를 쉽게 해결하고 생산성을 높이는 데 큰 도움이 됨

<br>

만약 앱의 프로세스의 생명 주기를 관리하고 싶다면 ProcessLifecycleOwner 를 살펴보아야함

<br>

<br>

#### **사용자 정의 LifecycleOwner 구현**

`support library 26.1.1` 이후로는 프래그먼트와 액티비는 LifecycleOwner를 이미 구현한 형태로 제공

<br>

LifecycleOwner를 직접 구현하도록 LifecycleRegistry 클래스를 사용할 수 있다면, 직접 이벤트를 포워딩 해야함

액티비티 이외에 다른 클래스에서 직접 생명 주기를 생성하고 관리하고 싶다면 LifecycleRegistry 를 사용해 본다.

```kotlin
class MyActivity : Activity, LifecycleOwner{
  private lateinit lifecycleRegistry : LifecycleRegistry
  
  @Overried
  fun onCreate(savedInstanceState : Bundle){
    super.onCreate(savedInstanceState)
    
    lifecycleRegistry = LifecycleRegistry(this)
    lifecycleRegistry.markState(Lifecycle.State.CREATED)
  }
  
  @Override
  fun onStart(){
    super.onStart()
    lifecycleRegistry.markState(Lifecycle.State.STARTED)
  }
  
  @NonNull
  @Override
  fun getLifecycle() : Lifecycle = lifecycleRegistry
}
```

<br>

<br>

---

