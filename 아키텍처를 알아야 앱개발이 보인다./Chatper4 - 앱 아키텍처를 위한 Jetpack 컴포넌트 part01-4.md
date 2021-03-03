### **DataBindingComponent 사용하기**

바인딩 어댑터에 대한 getter를 포함하는 인터페이스, 구현하는 클래스는 반드시 하나 이상의 메서드를 가져야함

메서드의 이름은 접두어 get과 바인딩 어댑터 클래스 또는 인터페이스 이름의 합성어

> @BindingAdapter 메서드를 가진 클래스 이름이 ClickBinding 이면 getClickBinding()

<br>

바인딩 어댑터를 사용하는 경우 리소스를 정리하는데 어려움이 있는데 DataBindingComponent는 이러한 문제점을

해결할 수 있도록 도와주며, 동적으로 바인딩 어댑터를 바인딩 클래스에 포함 가능하다.

<br>

<br>

---

<br>

### 양방향 데이터 바인딩

> 단방향 데이터 바인딩 vs 양방향 데이터 바인딩 예시 (동일한 기능을 수행)

> 단방향

```xml
<CheckBox
          ...
          android:checked="@{viewmodel.rememberMe}"
          android:onCheckedChanged="@{viewmodel.rememberMeChanged}" />
```

> 양방향

```xml
<CheckBox
          ...
          android:checked="@={viewmodel.rememberMe}"/>
```

표현식에서 `=` 기호가 추가된 형태로 변경됨

<br>

데이터들의 변화에 반응하도록 Observable을 구현한 레이아웃 변수를 사용

보통은 BaseObservable을 상속하고 멤버 getter 메서드에 @Bindable 어노테이션을 사용(java 의 경우)

```kotlin
class LoginViewModel : BaseObservable() {
    private val data = ""

    @get:Bindable
    var rememberMe: Boolean?
        get() = true
        set(value) {
          notifyPropertyChanged(BR.rememberMe)
        }
}
```

<br>

**사용자 정의 속성을 사용하는 양방향 바인딩**

사용자가 직접 정의한 속성에 대해 양방향 바인딩을 사용하려면 @InverseBindingAdapter와 @InverseBindingMethod 어노테이션을 사용해야함

<br>

> 양방향 바인딩을 "time" 이라는 속성을 가진 사용자 정의 뷰 에서 호출하는 예제

1.@BindingAdapter를 사용해 값이 변경될 떄 초깃값을 설정하고 업데이트 하는 메서드 생성

```kotlin
@BindingAdapter("time")
fun setTime(view:MyView, newValue:Time){
  if(view.time != newValue){
    view.time = newValue
  }
}
```

2.@InverseBindingAdapter를 사용하여 뷰에서 값을 읽는 메서드에 주석을 표시

```kotlin
@InverseBindingAdapter(attribute="time")
fun getTime(view:MyView) : Time = view.time
```

<br>

@InverserBindingAdapter 메서드는 역으로 레이아웃의 사용자 정의 속성값이 변경되었을 떄 뷰모델 등과 같은 레이아웃 변수에 변경 사항을 전달하여 양방향 바딘딩이 구현될 수 있게 함

<br>

하지만 이것만으로 레이아웃의 속성이 변경되었는지 알 수 없으므로 뷰에 대한 리스너를 설정해야한다.

> @BindingAdapter 메서드를 하나 더 추가하여 변경 사항에 대한 리스너를 추가 할 수 있다.

```kotlin
@BindingAdapter("timeAttrChanged")
fun setListeners(view:MyView, attrChange:InverseBindingListener){
  // 커스텀 뷰에 대한 클릭, 포커스, 터치 등의 원하는 변경 사항에 리스너를 추가
}
```

<br>

리스너는 InverseBindingListener를 매개 변수로 반드시 포함해야함. InverseBindingListener를 사용하여 데이터 바인딩 클래스 구현체에 속성의 변경 사항을 알릴 수 있음

<br>

모든 양방향 바인딩은 합성 이벤트 속성을 생성하는데, 이름은 기본 속성 이름과 같지만 접미사 'AttrChanged' 를 붙임

만약 기본값 대신 다른 접미어를 사용하려면 @InverseBindingAdapter의 event 멤버를 추가로 정의한다.

<br>

<br>

**양방향 바인딩에서 컨버터 사용하기**

뷰가 양방향 바인딩되고, 화면에 나타나기 전에 특정 포맷으로 변경하거나 변경 사항이 추가되어야 하는 상황이라면 컨버터를 사용할 수 있음

> EditText에 날짜를 표시하는 예제

```xml
<EditText
          ...
          android:text="@={Converter.dateToString(viewmodel.birthDate)}"
```

양방향 바인딩 표현식이 사용된다면, 반대로 문자열 형식의 날짜가 long 타입의 값으로의 변경도 필요

데이터 바인딩 라이브러리에서는 이러한 처리를 도와주는 @InverseMethod 어노테이션을 제공

> 예제

```kotlin
class Converter{
  @InverseMethod("stringToDate")
  fun dateToString(value:Long):String{
    //long to String
  }
  
  fun stringToDate(value:String):Long{
    //string to long
  }
}
```



---