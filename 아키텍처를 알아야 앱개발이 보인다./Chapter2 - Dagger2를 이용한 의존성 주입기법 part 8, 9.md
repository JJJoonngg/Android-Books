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

