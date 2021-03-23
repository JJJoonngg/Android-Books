## 3.LiveData

<br>

**관찰 가능한(Observable) 데이터 클래스.** 

<br>

컴포넌트들의 Lifecycle을 통해 생명 주기를 인식 하여 따른다. 데이터의 변경을 활성화된 관찰자(Observer)를 통해 알림

주어진 LifecycleOwner의 생명 주기가 STARTED 또는 RESUME 상태 인경우에만 활성상태로 간주한다.

LifecycleOwner 인터페이스를 구현하는 객체를 매개 변수로 하는 `observe()` 메서드를 통해 옵서버를 등록할 수 있음

옵서버의 구현체가 DESTOYED 상태가 되면 자동으로 옵서버는 내부에서 제거됨(메모리 누수를 걱정할 필요가 없어짐)

<br>

- **장점**
  - UI와 데이터 상태의 동기화
    - Observer 패턴을 따름. 생명 주기 상태 변화를 Observer에게 알림.
    - Observer 객체에서 데이터 변경에 따른 UI 를 갱신하려면 코드를 작성해야함.
  - 메모리 누수를 방지함
    - Observer는 Lifecycle에 바인딩되며, 생명 주기 상태가 DESTOYED 되면 스스로 정리함
  - 액티비티가 갑작스럽게 종료될 때도 안전
    - Observer가 비활성화된 상태일 때라도, LiveData로부터 어떠한 이벤트도 받지 않아 안전
  - 생명주기에 대한 고민을 안해도 된다.
    - LiveData에 LifecycleOwner를 위임하면 자동으로 모든 것을 관리한다.
  - 최신의 데이터 유지
    - 생명주기가 활성화 되는 시점에 최신 데이터를 다시 가져옴. 
    - 액티비티가 백그라운드인 상태에서 데이터가 변경되어도, 포그라운드 상태가 될 떄 최신의 데이터를 바로 받음
  - 구성 변경에 대응
    - 화면 회정 등과 같은 구성 변경으로 인해 재생성 되더라도 즉시 최신 데이터를 받을 수 있다.
  - 자원 공유
    - LiveData를 상속하여 singleton pattern 으로 사용할 수 있음.
    - android system service 와 같은 곳에 단 한 번만 연결하고, 앱 내 어디에서나 다중으로 접근하여 관찰 가능

<br>

<br>

### MutableLiveData를 이용한 데이터 쓰기

LiveData는 데이터 읽기만 가능하므로, 데이터를 쓰려면 MutableLiveData를 사용함

```kotlin
class MainActivity : AppCompatActivity{
  val liveString : MutableLiveData<String> = MutableLiveData()
  

  override fun onCreate(savedInstanceState:Bundle){
    super.onCreate(savedInstanceState)
    
    with(liveString){
      postValue("Hello Charles") //Data Write
      value = "Hello World" //Data Write
      observe(this ,{
        //어떤 값이 먼저 들어올까?        
      })
    }
  }
}
```

<br>

MutableLiveData 가진 데이터를 쓰는 메서드는 `setValue()`,  `postValue()` 메서드가 있으며 읽으려면 `getValue()`

- **`setValue()`**
  - 반드시 메인 스레드에서만 호출해야함
  - 백그라운드에서 사용시 IllegalStateException이 발생
  - 이미 활성화된 Observer를 가진다면, 변경된 데이터를 콜백 메서드로 부터 얻을 수 있음
- **`postValue()`**
  - 백그라운드 스레드에서 호출하는 용도로 사용
  - 주어진 값을 설정하는 태스크를 내부에서 핸들러를 통해 메인 스레드에 전달하기 때문(즉시 값을 설정하지 않음)
  - 메인 스레드가 실행 되기전에 여러 번 호출해도, 가장 마지막 설정된 값만 가져옴

> 위의 경우 Hello World 가 먼저, Hello Charles 가 나중에 실행됨

<br>

> MutableLiveData를 생성한 직후 초기 값이 null 이므로, 이를 방지하고자 다음과 같이 사용가능

```kotlin
class InitMutableLiveData<T>(initValue: T) : MutableLiveData<T>() {
    init {
        value = initValue
    }
}
```

<br>

<br>

### **상속을 통한 LiveData 사용하기**

LiveData는 Observer의 생명 주기가 STARTED 또는 RESUMED인 경우 활성화 상태로 간주함

> 주식 가격의 변동을 LiveData 클래스로 확장하는 방법을 보여주는 샘플 코드

```kotlin
 class StockLiveData(symbol: String) : LiveData<BigDecimal>() {
    private val stockManager: StockManager
    private val listener: SimplePriceListener = object : SimplePriceListener() {
        fun onPriceChanged(price: BigDecimal?) {
            value = price
        }
    }

    override fun onActive() {
        stockManager.requestPriceUpdates(listener)
    }

    override fun onInactive() {
        stockManager.removeUpdate(listener)
    }

    init {
        stockManager = StockManager(symbol)
    }
}
```

- **`onActive()`** : LiveData에 활성화된 관찰자가 있는 경우 호출됨. 
- **`onInactvie()`** : LiveData에 활성화된 관찰자가 전혀 없는 경우 호출됨.
- **`setValue()`** : LiveData의 값을 갱신하는데 호출되며, 활성화된 관찰자들에게 변경에 대해 알림

<br>

> StockLiveData 사용 예제

```kotlin
class MyFragment : Fragment {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val myPriceListener: LiveData<BigDecimal> = ...
        myPriceListener.observe(this, price->{
          //UI 갱신  
        })

    }
}
```

<br>

<br>

### **MediatorLiveData 사용하기**

MutableLiveData의 하위 클래스로 다른 여러 LiveData를 관찰하고 데이터의 변경에 반응

자신의 active/inactive 상태를 연결된 소스들에 전파함

예시로 2개의 LiveData 인스턴스가 있고, 이 둘의 데이터 변경 내용을 하나의 LiveData로 관리하고 싶다면 MediatorLiveDat에 두 개의 LiveData를 소스로 추가하면 됨

```kotlin
val liveData1: LiveData<> = ... 
val liveData2: LiveData<> = ... 
val liveDataMerger = MediatorLiveData<>() 
liveDataMerger.addSource(liveData1) { value -> result.setValue(value) } 
liveDataMerger.addSource(liveData2) { value -> result.setValue(value) }
```

<br>

<br>

### **LiveData 변형하기**

LiveData에 저장된 값을 관찰자로 전달하기 전에 이 값을 변경하거나 또는 다른 타입의 LiveData 인스턴스로 전달하고 싶을 경우 

Lifecycle 패키지에서 제공하는 Transformations 라는 클래스를 사용하면 된다.

<br>

#### **`Transformations.map()`**

첫 번째 매개 변수를 통해 입력된 소스 LiveData로 부터 새로운 타입의 LiveData를 만들 수 있음

두 번째 매개 변수 mapFunction에서 소스값을 원하는 새로운 값으로 반환함

```kotlin
val userLiveData : LiveData<> = ...
val userFulNameLiveData : LiveData<> = Transformations.map(userLiveData.value){user->
  user.firstName + user.lastName
}
```

> userLiveData가 변경될 때마다 userFullNameLiveData 또한 변경됨
>
> 메인 스레드에서 실행되므로 긴 작업은 지양

<br>

#### **`Transformation.switchMap()`**

`map()` 과 비슷하지만 변형시킨 데이터를 LiveData로 반환하는 점이 다름

입력된 소스 LiveData는 내부에서 생성한 MediatorLiveData에 추가되어 관리한다.

> 이름을 입력 받을 때 마다 이름으로 사용자 정보를 조회해야하는 예제

```kotlin
val nameQueryLiveData : MutableLiveData<String> = MutableLiveData()

fun getUsersWithNameLiveData() : LiveData<User> = Transformation.switchMap(nameQueryLiveData){ name->
  name -> dataSource.getUsersWithNameLiveData(name)
}
```

<br>

<br>

### **데이터 바인딩과 LiveData의 사용**

데이터 바인딩의 가장 중요한 기능 중 하나가 관찰성이다.

데이터 바인딩과 LiveData를 함꼐 사용시 생명 주기에 대한 걱정 없이 데이터 변경에 따른 UI 변경을 자동으로 처리 하도록 설정 가능하다.

<br>

**LiveData를 사용하면서 양방향 바인딩 사용하기**

setter 메서드의 추가로 양방향 바인딩의 구현과 LiveData의 사용을 동시에 할 수 있음

주의 해야 할 점은 메서드 시그니처

- 메서드의 이름과 바인딩 표현식에서 참조하는 멤버 이름이 일치해야함
- 메서드 매개 변수의 타입이 LiveData의 제네릭 타입과 일치해야함

```kotlin
class UserViewModel : BaseObservable{
  
  val name:MutableLiveData<String> = MutablaLiveData()
  
  fun getName() = name
  
  fun setName(name:String){
    this.name.value = name
  }
}
```

```xml
<data>
  <variable
            name="viewModel"
            type="com.charlezz.jetpacksample.UserViewModel" />
  ...
  <EdtiText
            ...
            android:text="@={viewModel.name}" 
            .../>
</data>
```

<br>

---

