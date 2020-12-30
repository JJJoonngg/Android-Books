## 8. Lazy 주입 과 Provider 주입

`Lazy<T>` 타입 또는 `Provider<T>` 타입을 사용한다면, 상황에 따라 의존성 주입의 시점을 늦추거나 새로운 객체를 요청 할 수 있다.



### Lazy 주입

`Lazy<T>`의 get() 메서드를 호출 하기 전까지는 객체가 초기화 되는 것을 늦출 수 있다.

```kotlin
@Component(modules = [CounterModule::class])
interface CounterComponent {

    fun inject(counter: Counter)
}
```

```kotlin
@Module
class CounterModule {
    var next = 100

    @Provides
    fun provideInteger(): Int {
        println("computing...")
        return next++
    }
}
```

```kotlin
class Counter {

    @Inject
    lateinit var lazy: Lazy<Int>

    fun printLazy() {
        println("printing...")
        println(lazy.value)
        println(lazy.value)
        println(lazy.value)
    }
}
```

```kotlin
@Test
fun testLazy() {
    val component: CounterComponent = DaggerCounterComponent.create()
    val counter = Counter()
    component.inject(counter)
    counter.printLazy()
}
```

**결과**

```
printing...

computing...

100

100

100
```



### Provider 주입

매번 새로운 인스턴스를 주입받고 싶을 때 사용 가능

Lazy 와 마찬가지로 바인드 된 타입(T) 를 제네릭으로 갖는 `Provider<T>`  타입을 만들면 됨

`Provider<T>`  의 get() 메서드를 호출할 때마다 새로운 객체를 제공 받음

```kotlin
class Counter {

    @Inject
    lateinit var provider: Provider<Int>

    fun printProvdier() {
        println("printing...")
        println(provider.value)
        println(provider.value)
        println(provider.value)
    }
}
```

```kotlin
@Test
fun testProvider(){
		val counterComponent = DaggerCounterComponent.create()
		val counter = Counter()
		counterComponent.inject(counter)
		counter.printProvider()
}
```

**결과**

```
printing...
computing...
100
computing...
101
computing...
102
```

매 Provider.get() 메서드 호출시 새로운 객체를 생성하므로 `computing...` 문구와 카운트가 1씩 증가하는 결과를 나타냄

컴포넌트가 `@Singleton`과 같은 특정 범위로 지정시에는 `Provider<T>` 를 사용한다고 하더라도 바인드된 의존성은 싱글턴으로 관리되어 같은 인스턴스를 제공받는다.



---





## 9. 한정자 지정하기

### @Named 사용하기

때로 반환형으로 바인드된 객체를 식별하기에는 충분하지 않을 수 있다. 예시로 하나의 컴포넌트에 바인드 되면서 String을 반환하는 `@Provides` 메서드가 두개 이상인 경우를 생각하면 Dagger 입장에서는 어느 쪽을 바인딩해야 할지 애매모호해져 에러를 발생시킴

```kotlin
@Module
class MyModule{

		@Provides
		fun provideHello() = "Hello"
		
		@Provides
		fun provideWorld() = "World"
		
		//String 타입을 반환하는 메서드가 두 개이므로 에러
}
```



상황에 따라 반환형이 같은 두 개 이상의 `@Provides` 메서드를 바운드해야 할 수도 있다. 그럴 땐 `@Named`애노테이션을 통해 같은 타입의 의존성을 식별 할 수 있다.

```kotlin
@Component(modules = [MyModule::class])
interface MyComponent {
		fun inject(myClass : MyClass)
}
```

```kotlin
@Module
class MyModule{

		@Provides
		@Named("hello")
		fun provideHello() = "Hello"
		
		@Provides
		@Named("world")
		fun provideWorld() = "World"
		
}
```

 `@Named` 의 속성으로 hello, world 를 지정했다. 이제 같은 타입이지만 한정자로 구분할 수 있게 된 것

의존성을 주입받는 곳에서도  `@Named` 를 지정해야한다.

```kotlin
class MyClass{
		@Inject
		@Named("hello")
		var strHello : String? = null
		
    @Inject
		@Named("world")
		var strWorld : String? = null
}
```

```kotlin
@Test
fun myComponent(){
		val myClass = MyClass()
		DaggerMyComponent.create().inject(myClass)
		println(myClass.strHello)
		println(myClass.strWorld)
}
```

```
결과
Hello
World
```



### 사용자 정의 한정자 만들기

`@Named` 가 아닌 고유 한정자를 만들 때 , `@Qualifier` 를 사용하여 직접 한정자를 만들 수도 있다.

```kotlin
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Hello {
}
```

Hello 애노테이션을 정의했으므로 이제 `@Named` 대신 `@Hello` 를 사용할 수 있다.

```kotlin
@Module
class MyModule{

		@Provides
		@Hello
		fun provideHello() = "Hello"
		
		@Provides
		fun provideWorld() = "World"
}
```

```kotlin
class MyClass{
		@Inject
		@Hello
		var strHello : String? = null
		
    @Inject
		var strWorld : String? = null
}
```

```kotlin
@Test
fun myComponent(){
		val myClass = MyClass()
		DaggerMyComponent.create().inject(myClass)
		println(myClass.strHello)
		println(myClass.strWorld)
}
```

```
결과
Hello
World
```





---



## 10.범위 지정하기

각 컴포넌트는 `@Scope` 애노테이션과 함께 범위를 지정할 수 있음

컴포넌트의 구현과 함께 각 컴포넌트 인스턴스는 의존성의 제공 방법에 대한 동일성을 보장받을 수 있다. 하나의 인스턴스만 만들어서 참조하는 싱글턴 패턴과 비슷한 개념이지만, 애플리케이션의 생명 주기와 달리 생명 주기를 따로 관리할 수 있다는 점에서 차이가 있다고 볼 수 있다.



### @Singleton 사용하기

일반적으로 `@Singeton` 애노테이션을 사용하여 범위를 지정하여 객체를 재사용할 수 있다.

```kotlin
@Singleton
@Component(modules =[MyModule::class])
interface MyComponent{
		fun getObject() : Object
}
```

```kotlin
@Module
class MyModule{
		@Provides
		@Singleton
		fun provideObject() = Object()
}
```

컴포넌트와 메서드에 `@Singleton` 을 추가한 후 테스트 코드를 통해 동일한 인스턴스를 제공받을 수 있는지 확인해 본다.

```kotlin
@Test
fun testObjectIdentity(){
		val myComponent = DaggetMyComponent.create()
		val temp1 = myComponent.getObject()
		val temp2 = myComponent.getObject()
		asertNotNull(temp1)
		asertNotNull(temp2)
		assertSame(temp1, temp2)
}
```

```
결과
326549596
326549596
true
```





### @Reusable 사용하기

`@Reusable`도 `@Singleton` 을 비롯한 다른 커스텀 스코프와 비슷한 역할을 함

특정 컴포넌트 스코프에 종속되지 않아 컴포넌트에 `@Reusable` 을 선언하지 않아도 됨

다른 스코프 애노테이션 처럼 인스턴스의 동일성을 보장하진 않지만, 항상 동일한 인스턴스를 사용해야 하는 게 아니라면 메모리 관리 측면에서 조금 더 효율적





### @Scope 확장하기

커스텀 스코프를 직접 만들어 컴포넌트의 범위를 지정할 수 있다.

```kotlin
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UserScope{
}
```

```kotlin
@Module
class MyModule{
		@Provides
		@UserScope
		fun provideObejct() = Object()
}
```



---

