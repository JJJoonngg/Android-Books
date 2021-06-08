## 4. ViewModel

<br>

생명주기를 인식하며, UI와 관련된 데이터를 저장하고 관리하는 클래스, 화면 회전 같은 구성 변경(Configuration changes)에서도 살아남아 데이터를 보존

<br>

#### **ViewModel 구현하기**

ViewModel 객체는 구성 변경에도 자동으로 유지되므로, 보유한 데이터는 다음 액티비티 또는 프래그먼트 인스턴스에 즉시 사용 가능함.

> UI 컨트롤러 대신 사용자 목록을 불러오고 데이터를 유지하는 ViewModel

```kotlin
class MyViewModel : ViewModel() {
  private val _users : MutableLiveData<List<User>> = mutableLiveData()
  val users : LiveData<List<User>> = _users
  
  fun loadUsers(){
    //이곳에서 비동기적으로 사용자 목록을 불러옴
  }
}
```

불러온 데이터는 일반적으로 LiveData로 관리하길 권장하며, 액티비에서는 다음과 같이 접근 가능

```kotlin
...
override fun onCreate(savedInstanceState : Bundle){
  //onCreate 에서 ViewModel 을 생성
  //activity 가 재생성되어 onCreate()가 또 호출되더라고 이전에 만든 ViewModel 인스턴스를 가져옴
  
  val model : MyViewModel by viewModels()
  
  model.users.observe(this, Observer{users->
     //LiveData를 관찰하고 이곳에서 사용자 목록을 얻음
	})
}
```

activity 가 재생성되도라도, 최초 생성한 MyViewModel 인스턴스를 그대로 가져오고, ViewModel을 생성한 activity가 종료되어야 ViewModel 의 `onCleared()` 가 호출되고, 리소스를 정리함

<br>

ViewModel 객체는 View 또는 LifecycleOwner 보다 오래 지속되도록 설계 되었으며, View 및 Lifecycle에 대한 의존성이 없으므로 ViewModel에 대한 단위 테스트를 더 쉽게 작성할 수 있게 해준다.

생명 주기 이벤트를 구현하도록 LifecycleObserver를 포함할 순 있지만, ViewModel 내부에서 LiveData의 변경 사항을 관찰해서는 안된다.

---

<br>

#### **ViewModel 의 생명주기**

<img src= "img/viewmodel-lifecycle.png">

> 출처 : https://developer.android.com/topic/libraries/architecture/viewmodel#lifecycle

일반적으로 ViewModel 객체의 생성은 액티비티의 첫 `onCreate()` 메서드 호출에서 이루어지고, 구성 변경에 의해 여러번 액티비티가 재생성되어 

`onCreate()` 호출이 여러번 되더라도 최초에 생성된 ViewModel 인스턴스를 유지한다.

<br>

---

#### **Fragment 간 데이터 공유하기**

activity scope 로 생성한 ViewModel은 fragment 사이에서 데이터를 공유하고 상호 작용할 수 있도록 한다.

<br>

> activity scope 로 생성한 SharedViewModel이 있다고 가정, MasterFragment 와 DetailFragment가 공유하는 방법을 확인
>
> KTX ViewModel 을 활용한 예제
>
> 참고 : https://thdev.tech/androiddev/2020/07/13/Android-Fragment-ViewModel-Example/

```kotlin
class SharedViewModel : ViewModel() {
    private val _selected: MutableLiveData<Item> = MutableLiveData()
    val selected: LiveData<Item> = _selected

    fun select(item: Item) {
        _selected.value = item
    }
}
```

```kotlin
class MasterFragment : Fragment() {
    private val model: SharedViewModel by activityViewModels()
		...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemSelector.setOnClickListener { item ->
            model.select(item)
        }
    }
}
```

```kotlin
class DetailFragment : Fragment() {
    private val model: SharedViewModel by activityViewModels()
		...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.selected.observe(this, Observer{ item ->
             // UI 갱신하기          
        })
    }
}
```

**장점**

- ViewModel 을 사용하면, 액티비티는 아무것도 하지 않아도 됨
- 프래그먼트는 SharedViewModel을 사용하는데 선행되어야할 작업이 없고, 프래그먼트 중 하나가 사라지더라도 여전히 잘 동작함
- 각각의 프래그먼트는 자신만의 생명 주기를 가지며, 이러한 점이 다른 프래그먼트에 영향을 주지 않음

<br>

---

#### **ViewModel 사용 시 주의해야 할 점**

<br>

- **절대로 액티비티 같은 Context를 참조해서는 안됨**
  - 액티비티는 언제든지 파괴되고, 재생성되는 반면, ViewModel은 유지가 된다. 이때 이전에 파괴된 액티비티를 ViewModel 에서 유지함으로 메모리 누수가 발생
  - Context 필요시 AndroidViewmodel을 사용한다.
- **Android Framework 코드를 참조하지 않도록 한다.**
  - 단위 테스트가 힘들어지게 되는 경우가 발생, View 에 필요한 최소한의 데이터만 갖는게 좋음
  - 일반적인 비지니스 로직은 다른 계층에서 수행
- **Dagger2와 ViewModel의 사용을 신중하게 한다.**
  - Dagger2는 자신만의 Scope를 지정하고 인스턴스를 관리, ViewModel 또한 스스로 인스턴스를 관리
  - 이 둘의 Scope 는 다르므로 같이 사용되는 경우, 액티비티가 재생성되었을 때 객체의 동일성이 꺠지기 쉬움
- **ViewModel 에서 getString(Int) 를 통해 문자열을 관리하는 경우, 시스템 언어를 변경해도 이전의 문자열이 그대로 남음**
  - View가 바인딩될 때, 리소스 아이디를 참조하여 올바른 언어의 문자열을 참조할 수 있도록 해야함

<br>

---

