## 3. Dagger2란 무엇인가?

[Dagger2](https://github.com/google/dagger)는 자바와 안드로이드를 위한 강력하고 빠른 의존성 주입 프레임워크다. 리플렉션을 사용하지 않고, 런 타임에 바이트 코드도 생성하지 않는 것이 특징이다. 컴파일 타임에 애노테이션 프로세서에 의해 의존성 주입과 관련된 모든 코드를 분석하고 자바 소스 코드를 생성한다.



다음과 같은 이유로 인해 Dagger가 어렵다고 느낄 것 같다.

- 애노테이션 기반의 코드 생성 방식이 익숙하지 않은 사람은 내부가 어떤 식으로 동작하는지 유추하기 힘들다.
- 다른 DI 라이브러리보다 공부해야 할 부분이 더 많다.
- Component, Subcomponent, Module 등과 같은 Dagger에서만 사용하는 용어로 인해 혼란스럽다.

하나씩 용어와 개념을 익히면 그렇게 어려운 일도 아니니 시작하기도 전에 너무 겁먹지 않도록 한다.



Dagger의 장점

- 자원 공유의 단순화. 지정된 범위의 생명 주기 내에서 동일 인스턴스를 제공한다.
- 복잡한 의존성을 단순하게 설정함. 애플리케이션이 커질수록 많은 의존성을 갖는데 Dagger는 이를 쉽게 제어해 준다.
- 유닛 테스트를 쉽게 도와준다.
- 자동 코드 생성. 생성된 코드는 명확하고 디버깅이 가능하다.
- Dagger2는 난독화 문제가 없다. Dagger1은 리플렉션의 사용으로 인해 런타임에 성능 및 난독화와 고나련된 문제가 발생했다.
- 라이브러리 크기가 작다.





---



## 4. 프로젝트에 Dagger 설정하기

해당 chapter 는 샘플 프로젝트를 만들어서 진행한다.



---



## 5. 첫 번째 의존성 주입 구현하기

'프로젝트에 Dagger 설정하기'를 참고하여 app 모듈에 Dagger2를 적용한다.

설정이 끝났다면 "Hello World" 문자열을 제공할 모듈을 만든다.

- MyModule.kt

  ```kotlin
  @Module
  class MyModule {
      @Provides
      fun provideHelloWorld(): String = "Hello World"
  }
  ```

Dagger는 컴파일 타임에 `@Module` 과 `@Provides` 애노테이션을 읽고 의존성 주입에 필요한 파일들을 생성한다.



애노테이션의 용법에 대한 간단한 설명은

`@Module` : 의존성을 제공하는 클래스에 붙임

`@Provides` : 의존성을 제공하는 메서드에 붙임

그러므로 `MyModule.kt` 는 의존성을 제공하는 클래스, `provideHelloWorld()` 메서드는 String 타입의 Hello World 문자열을 제공하는 것을 뜻한다.



`MyModule` 클래스 하나만으로는 모듈을 참조하는 컴포넌트가 없기 때문에 별도의 클래스 파일이 생성되지 않는다. 

따라서 컴포넌트를 만들어야한다.

```kotlin
@Component(modules = [MyModule::class])
interface MyComponent {
    fun getString(): String //프로비전 메서드, 바인드된 모듈로부터 의존성을 제공
}
```

`@Component` 가 붙은 `MyComponent` 인터페이스 내에는 제공할 의존성들을 메서드로 정의해야 하며, `@Component` 에 참조된 모듈 클래스로부터 의존성을 제공받는다. 컴포넌트 메서드의 반환형을 보고 모듈과 관계를 맺으므로 바인드된 모듈로부터 해당 반환형을 갖는 메서드를 찾지 못한다면 컴파일 타임에 에러가 발생한다.



Dagger는 컴파일 타임에 `@Component` 를 구현할 클래스를 생성하는데, 이때 클래스의 이름은 'Dagger' 라는 접두어가 붙는다.

- ex ) MyCmoponent ==> DaggerMyComponent



Android Framework 에 대한 의존성이 없으므로, JUnit 테스트 클래스 작성을 통해 의존성의 제공 여부를 확인한다.

```kotlin
class ExampleUnitTest {
    @Test
    fun testHelloWorld() {
        val myComponent: MyComponent = DaggerMyComponent.create()
        println("result = ${myComponent.getString()}")
    }
}
```





---



## 6. 모듈

모듈은 컴포넌트에 의존성을 제공하는 역할을 한다. 클래스에 @Module 애노테이션을 붙이는 것으로 간단히 모듈 클래스를 만들 수 있다.



#### 프로바이더

모듈 클래스 내에 선언되는 메서드에는 `@Provides` 애노테이션을 붙이는 것으로 컴파일 타임에 의존성을 제공하는 바인드된 프로바이더를 생성.

**메서드의 반환형을 보고 컴포넌트 내에서 의존성이 고나리되어 중복되는 타입이 하나의 컴포넌트 내에 존재하면 안됨.**

```kotlin

@Module
class DuplicationModule{
    @Provides
    fun provideHelloWorld() = "Hello World"
    
    @Provides
    fun provideCharles()  = "Charles" // 동일한 타입이 2개 이상 존재하므로 에러 
}
```



컴포넌트 내 바인드된 메서드의 반환형은 @Provides 메서드의 매개 변수로 사용할 수 있다. 

```kotlin

@Module
class MyModule {

    @Provides
    fun provideName() = "Charles"

    @Provides
    fun provideAge() = 100

    @Provides
    fun providePerson(name: String, age: Int): Person = Person(name, age) 
  	//name = Charles, age=100

}
```

매개 변수 타입에 맞는 의존성이 컴포넌트 또는 컴포넌트와 바인드된 모듈에 없다면 에러가 발생한다.



**모듈 클래스가 추상 클래스인 경우 @Provides 메서드는 statics 메서드여야만한다.(Java)**

```java
@Module
public abstract class MyModule{
		@Provides
		static String provideName(){
					return "Charles";
		}
}
```





#### Null 의 비허용

`@Provides` 메서드는 null 을 반환하는 것을 기본적으로 제한한다. 메서드에서 null 을 반환하는 경우 컴파일 타임에 NPE 을 발생시키다. `@Provides` 메서드의 반환값이 null 인 것을 명시적으로 허용하려면 메서드에 `@Nullable`을 추가해야한다.



프로비전 메서드뿐만 아니라 멤버-인젝션 메서드를 써서 Null 을 주입하는 경우에도 멤버 변수에 `@Nullable` 을 꼭 붙여야 한다.

---

*나는 kotlin 으로 코드를 작성했으며 `?` 를 이용하여 `@Nullable` 애노테이션을 붙이지 않고 해당 챕터를 구현했다.*

*Java 로 구현할 경우에는 `@Nullable`애노테이션이 필요한 것으로 보인다.*



- MyModule

```kotlin
@Module
class MyModule {
    @Provides
    fun provideInteger(): Int? = null
}
```



- MyComponent

```kotlin
@Component(modules = [MyModule::class])
interface MyComponent {
    fun getInt(): Int?
}
```



- ExmapleUnitTest

```kotlin
class ExampleUnitTest {
    @Test
    fun testHelloWorld() {
        val myComponent: MyComponent = DaggerMyComponent.create()
        println("nullable check = ${myComponent.getInt()}")
      	//nullable check = null
    }
}
```

---



#### 모듈의 상속

`@Module` 애노테이션을 가질 수 있는 속성 중 includes 라는 것이 있다. includes에 다른 모듈 클래스들의 배열을 정의하는 것만으로 `@Provides` 메서드의 상속이 가능하다.

- Modeul A 와 B 가 존재하고 B 가 A 를 상속하는 코드

```kotlin
@Module
class ModuleA{
	@Provides
	fun provideA() = A()
}
```

```kotlin
@Module(includes =[ModuleA::class])
class ModuleB{
	@Provides
	fun provideB() = B()
}
```



컴포넌트를 선언시 B 를 참조하는 경우 A를 상속해 A 타입의 객체도 바인딩 된다.

**주의할 점 - 모듈간 상속 시 중복되는 타입이 존재하면 안됨**

이 점을 주의해서 모듈을 설계한다면, 보일러 플레이트 코드를 많이 제거할 수 있다.

---