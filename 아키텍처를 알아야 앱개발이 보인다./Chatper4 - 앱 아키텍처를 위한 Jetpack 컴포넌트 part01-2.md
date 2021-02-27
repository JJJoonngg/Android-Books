#### 이벤트 처리하기

`onClick()` 메서드 등과 같은 이벤트를 뷰로부터 가져와서 처리할 수 있는 기능 제공

이벤트 속성 이름들은 리스너 메서드의 이름을 따름 ( ex) `View.OnClickListener` 가 가진 메서드는 `onClick()` 따라서 이벤트 속성의 이름은 `android:onClick()`

<br>

이벤트를 다루는 방법에는 메서드 참조와 리스너 바인딩 두가지가 존재

- **메서드 참조** : 리스너 메서드의 시그니처를 따라 참조하는 방식, 바인딩 표현식이 null 이면 리스너를 생성하지 않고 리스너를 null로 설정
- **리스너 바인딩** : 떼이터 바인딩은 대상 객체의 null 여부와 관계없이 항상 리스너를 만들고 뷰에 리스너를 설정

둘의 가장 큰 차이는 데이터 바인딩이 일어날 때 실제 리스너의 생성 여부

만약 이벤트 발생시 바인딩 표현식을 평가하고 싶다면 리스너 바인딩이 좋음

<br>

**메서드 참조**

이벤트를 핸들러 메서드에 직접적으로 바인딩 할 수 있음. 컴파일 타임에 에러를 확인할 수 있는 장점이 있다.

```kotlin
class MyHandlers{
  fun onClickFriend(view:view){...}
}
```

```xml
<layout>
  <data>
    <variable name ="handlers" type="...MyHnandlers"/>
    <variable name ="user" type="...User"/>
  </data>
  <LinearLayout
                ...>
    <TextView
              ...
              android:text="@{user.firstName}"
              android:onClick="@{handlers::onClickFriend}"/>
    
  </LinearLayout>
</layout>
```

바인딩 표현식에서 이벤트 리스너의 메서드 시그니처와 핸들러의 메서드 시그니처가 정확하게 일치해야함

<br>

**리스너 바인딩**

이벤트가 발생할 때 실행하는 바인딩 표현식. 메서트 참조와 비슷하지만 메서드 참조의 경우 바인딩 표현식을 임의로 실행

반환되는 타입이 이벤트 리스너의 반환 타입과 일치 시킴

```kotlin
class Presenter{
  fun onSaveClick(task:Task){}
}
```

```xml
<layout>
  <data>
    <variable name ="task" type="...Task"/>
    <variable name ="presenter" type="...Presenter"/>
  </data>
  <LinearLayout
                ...>
    <Button
            ...
            onClick="@{()-> presenter.onSaveClick(task)}"/>
    
  </LinearLayout>
</layout>
```

바인딩 표현식에서 콜백이 사용될 때 데이터 바인딩은 자동으로 필요한 리스너를 생성하고 해당 뷰에 리스너를 설정함

뷰에서 이벤트가 발생시 데이터 바인딩은 주어진 표현식을 평가함. 이러한 평가가 진행되는 동안 null과 스레드 안정성이 확보됨

<br>

리스너 바인딩은 두가지 옵션을 제공

- 모든 매개 변수를 무시하고 사용하지 않는 것
- 매개 변수의 이름을 정하고 바인딩 표현식에서 그것들을 사용하는 것

```kotlin
class Presenter{
  fun onSaveClick(view:View, task:Task){}
}
```

```xml
android:onClick="@{(view)-> presenter.onSaveClick(view, task)}"
```

parameter 가 2개 이상일 경우

```kotlin
class Presenter{
  fun onCompletedChanged(task:Task, completed:Boolean){}
}
```

```xml
android:onCheckedChanged="@{(cb, isChecked) -> presenter.onCompleteChanged(task, isChecked)}"
```

만일 리스닝을 하고 있는 이벤트의 반환형이 void 가 아니면 표현식에서도 같은 타입의 값을 반환해야함

<br>

바인딩 표현식에서 null 객체 때문에 평가 될 수 없는경우 데이터 바인딩은 해당 타입의 기본값을 반환

만약 void 타입을 반환하는 람다 표현식에서 삼항 연산자를 사용해야 하는 경우 void 키워드를 그대로 사용가능

<br>

리스너 표현식 사용시 데이터를 전달하는 수준의 간단한 코드로 작성하는 것을 추천(유지 보수 측면)

#### <br>

---

<br>

#### **Observable 데이터 객체로 작업하기**

데이터의 변경 사항을 감지하고 알려주는 객체

Observable 클래스에는 필드, 객체, 컬렉션 3가지 타입이 존재

<br>

**Observable 필드의 사용**

데이터 바인딩에서 Observable을 이미 구현한 몇몇 클래스를 제공함

- ObservableBoolean
- ObservableByte
- ObservableChar
- ObservableShort
- ObservableInt
- ObservableLong
- ObservableFloat
- ObservableDouble
- ObservableParcelable

필드 접근 시에 박싱과 언박싱 과정을 방지하려고 원시 타입만을 사용함

<br>

**Observable 컬렉션 사용**

몇몇 앱은 동적인 구조를 사용하여 데이터를 관리하는데 컬렉션은 리어한 구조에 접근하도록 키를 사용함

```xml
...
<import type="android.databinding.ObservableMap"/>
<variable name="user" typ="ObservableMap<String, Object"/>
...
...
<TextView
          anroid:text="@{user.lastName}"
          .../>
<TextView
          android:text="@{String.valueOf(1+(Integer)user.age)}"
          .../>
```

<br>

**Observable 객체 사용하기**

Observable 인터페이스를 구현한 클래스는 데이터의 변경에 대한 알림을 받으라는 리스너를 등록할 수 있음

인터페이스는 추가와 제거 리스너를 위한 방법을 가지지만, 반드시 데이터의 변경 알림 시기를 직접 정의해야함.

개발듸 편의성을 위해 데이터 바인딩 라이브러리는 BaseObservable 클래스를 제공. 

BaseObservable을 구현한 데이터 클래스는 프로퍼티 변경 시 알림을 책임짐. 이는 `@Bindale` annotation 을 getter 메서드에 적용하고 `notifyPropertyChange()` 메서드를 setter 메서드 내에서 호출하는 것으로 적용

<br>

만약 기존 데이터 클래스가 BaseObservable과 같은 베이스 클래스를 상속하지 못하는 구조라면 Observable 인터페이스의 구현과 PropertyChangeRegistry의 사용을 통해 리스너의 등록과 알림을 구현함.

<br>

---

<br>

#### 즉각적인 바인딩 하기

바인딩된 데이터가 변경시, 바인딩 클래스는 변경 사항을 스케줄링하여 다음 프레임에 반영될 수 있도록함. 그러나 즉각적인 데이터 바인딩 실행이 필요할 경우에는 `executePendingBinding()` 메서드를 호출하여 강제로 바인딩을 실행 시킬 수 있음

```kotlin
viewHolder.getBinding().setData(getItem(position))
viewHolder.getBinding().executePendingBinding()
```

<br>

---

<br>

#### BR 리소스 아이디로 바인딩 하기

특정 바인딩 클래스를 모르는 경우가 존재할 수 있음. 예시로 RecyclerView.Adapter 에서 뷰홀더를 생성시 뷰 타입이 여러개인 경우 바인딩 클래스를 특정 지을 수 없음

이런 경우 `onBindViewHodler()` 메서드에서 데이터를 바인딩시킬 때 문제가 되는데, 바인딩 클래스 내의 변수명을 참조하여 데이터 바인딩 시키는 방법을 확인한다.

```kotlin
@Override
fun onBindViewHolder(holder:BindingHolder, position:Int){
  val item:T = items.get(position)
  holder.binding.setVariable(BR.item, item)
  holder.binding.executePendingBinding()
}
```

<br>

---