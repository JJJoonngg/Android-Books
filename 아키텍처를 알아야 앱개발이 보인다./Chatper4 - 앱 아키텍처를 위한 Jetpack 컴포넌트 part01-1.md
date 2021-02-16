# 4. 앱 아키텍처를 위한 Jetpack 컴포넌트

Jetpack 컴포넌트는 생산성을 높여 개발할 수 있게 돕는 라이브러리, 도구, 가이드의 모음.

Jetpack 에서 애플리케이션을 설계하는 권장 사항을 따르고, 보일러 플레이트 코드를 줄이고, 복잡한 작업을 간소화 함으로써 중요한 코드에만 집중할 수 있음

<br>

크게 기초(Foundation), 아키텍처(Architecture), 동작(Behavior), 사용자 인터페이스(User Interface) 4개의 카테고리로 분류 되는데 이 책에서는 아키텍처에 대해서만 다룸

<br>

<br>

## 1. 데이터 바인딩

명령형 방식이 아닌 선언적 형식으로 레이아웃의 UI 구성 요소를 앱의 데이터와 결합할 수 있는 라이브러리

<br>

선언형 프로그래밍 : 문제에 대한 답을 저의하기 보다는 문제를 설명하는 것

명령형 프로그래밍은 "어떤 방법" 으로 할지 중점을 두는 반면에 선언형 프로그래밍 언어는 "무엇" 을 할지에 중점을 둠

> 명령형 프로그래밍 예제

```kotlin
val textView = findeViewById(R.id.sample_text)
textView.text = viewModel.userName
```

> 데이터 바인딩을 사용한 선언형 프로그래밍 예제

```xml
<TextView
	android:text = "@{viewmodel.userName}" />
```

<br>

<br>

#### 데이터 바인딩 설정하기

데이터 바인딩을 프로젝트에 설정하려면 애플리케이션 모듈의 build.gradle 에 다음 내용을 추가

```
android{
	...
	dataBinding{
			enabled = true
	}
}
```

> 다음 기능이 활성화

- 구문 강조
- 데이터 바인딩 표현식 오류 검출
- XML 코드 자동 완성
- 빠른 코드참조

<br>

<br>

#### 바인딩 클래스 생성하기

데이터 바인딩 라이브러리는 레이아웃의 변수와 뷰를 참조할 수 있는 바인딩 클래스를 생성

모든 바인딩 클래스는 ViewDataBinding을 상속.

xml 레이아웃 파일에서 가장 상위 레이아웃을 `<layout>` 태그로 감싸면 바인딩 클래스가 자동 생성

이름은 레이아웃 파일의 이름을 기반으로 결정됨

> activity_main.xml => ActivityMainBinding

레이 아웃에 대한 표현은 ~Binding 클래스로 작성 되지만, 실제 비즈니스 로직을 추적 또는 디버깅 하려면 ~BindingImpl 을 참조해야함

<br>

<br>

#### 바인딩 클래스로 바인딩 객체 생성하기

> inflate() 메서드를 사용해서 레이아웃 전개화 함께 바인딩 객체 생성

```kotlin
val bidning = ActivityMainBinding.inflate(layoutInflater)
```

<br>

> 레이아웃 전개하지 않고 전개 후에 바인딩 하는 경우 bind() 메서드를 사용

```kotlin
val binding = ActivityMainBinding.bind(rootView)
```

<br>

> 바인딩 클래스 이름을 미리 알지 못하는 경우 DataBindingUtil 클래스를 활용

```kotlin
val binding = DataBindingUtil.inflate(
  		layoutInflater,
  		R.layout.activity_main,
  		parent,
  		attachToParent
)
```

```kotlin
val binding = DataBindingUtil.bind(rootView)
```

<br>

> activity 의 setContentView를 대체 가능

```kotlin
class MainActivity : AppCompatActivity{
	lateinit var binding
	
	@Override
	fun onCreate(saveInstanceState : Bundle){
		super.onCreate(saveInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
	}
}
```

<br>

<br>

#### `<import>` 사용하기

`<data>` 태그 내에 `<import>` 태그를 사용하여, 참조하고 싶은 클래스를 레이아웃 파일에 간단히 불러올 수 있음

```xml
<data>
	<import type="android.view.View"/>
</data>
```

<br>

View 클래스 참조를 통해 View.VISIBLE, View.GONE 과 같은 상수를 참조할 수 있음

```xml
<TextView
	...
	androdi:visibility="@{user.isAdult ? View.VISIBLE : View.GONE}"/>
```

<br>

같은 이름을 갖는 클래스 두개 이상을 참조해야 하는 경우에 충돌을 피하고자 클래스의 이름을 변경하여 참조할 수 있음

> com.example.real.estate.View 클래스를 Vista로 변경하는 예제

```xml
<import type = "android.view.View"/>
<import type = "com.example.real.estate.View"
				alias = "Vista"/>
```

<br>

불러온 타입을 사용하여 변수를 선언할 때 사용할 수도 있음

```kotlin
<data>
	<import type="com.example.User"/>
	<import type="java.util.List"/>
	<variable name = "user" type="User"/>
	<variable name = "userList" type="List&lt;User"/>
</data>
```

불러온 타입을 사용 바인딩 표현식에 캐스팅도 가능

```xml
<TextView
          ...
          android:text = "@{((User)(user.connnection)).lastName}"
          ... />
```

불러온 타입의 static 필드나 메서드도 참조 가능

> java.lang.* 은 자동으로 import 되므로 생략 가능

```xml
<data>
  <import type="com.example.MyStringUtils"/>
  <variable name="user" type="com.example.User"/>
</data>
...
<TextView
          ...
          android:text="@{MyStringUtils.capitalize(user.lastName)}"
          ... />
```

<br>

<br>

#### `<include>` 사용하기

레이아웃 파일 내에서 다른 레이아웃을 포함하는 경우 `<include>` 태그를 사용할 수 있음

`<include>` 에 참조되는 레이아웃 파일 또한 데이터 바인딩을 사용하는 경우 app 네임 스페이스와 변수 이름을 사용하여 데이터를 넘깃길 수 있다

> activity_main.xml 에서 contact.xml 로 User 를 전달하는 예제

> activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout ...>
  
  <data>
    <variable
              name="user"
              type="com.charlezz.jetpacklibrarysample.User"/>
  </data>
  
  <LinearLayout
                android:id="@+id/root"
                ...>
    
    <include layout="@layout/contact"
             app:user="@{user}"/>
  </LinearLayout>
  
</layout>
```

> contact.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout ...>
  
  <data>
    <variable
              name="user"
              type="com.charlezz.jetpacklibrarysample.User"/>
  </data>
  
  <TextView
            ...
            adnroid:text="@{user.contact}"/>
  
</layout>
```

<br>

**주의해야 할 점 :  데이터 바인딩은 `<merge>`하위에 `<include>` 를 허용하지 않음**

<br>

---