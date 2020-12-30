## 11. 바인딩의 종류



### @Binds

`@Binds` 애노테이션은 모듈 내의 추상 메서드에 붙일 수 있으며, 반드시 하나의 매개 변수만을 가져야함

매개 변수를 반환형으로 바인드할 수 있으며, `@Provides` 메서드 대신 효율적으로 사용가능

```kotlin
@Binds
abstract fun bindRandom(secureRandom : SecureRandom) : Random
```

이미 바인드된 SecureRandom 을 Random 타입으로 한 번 더 바인드할 수 있다.



### @BindsOptionalOf

`@BindsOptionalOf` 애노테이션은 모듈내의 추상 메서드에 붙일 수 있으며 매개변수를 가질 수 없다.

void 가 아닌 특정 타입을 반환형으로 가져야 하며, 예외 사항을 던질 수도 없다.

```kotlin
@Module
abstract class CommonModule{
		@BindsOptionalOf
		abstract fun bindsOptionalOfString() : String
}
```

```kotlin
@Module
class HelloModule{
		@Provides
		fun provideString() = "Hello"
}
```

`@BindsOptionalOf` 메서드를 통한 의존성 주입은 다음과 같은 Optional 타입 등으로 주입된다.

```kotlin
class Foo{
		@Inject
		lateinit var str : Optional<String>
		
		@Inject
		lateinit var str2 : Optional<String>
		
		@Inject
		lateinit var str3 : Optional<Lazy<String>>
}
```

만약 컴포넌트 내에 Foor가 바인드 된 적이 있다면 Optional 의 상태는 present 이고 그렇지 않다면 absent 이다

이와 같이 어떤 타입의 의존성이 바인드되었는지 여부와 관계없이 `@Inject` 를 이용해 주입할 수 있는 것이 특징이다.

Optional 은 null 을 포함하는 것을 허용하지 않는다. 그러므로 `@Nullable` 바인딩에 대해서는 컴파일 타임에 에러를 발생시킨다.



바인드 유무에 따른 Optional 상태 테스트를 위해 두개의 컴포넌트를 생성해 본다.

```kotlin
@Component(modules = [CommonModule::class, HelloModule::class])
interface StrComponent {
    fun inject(foo :Foo)
}
```

```kotlin
@Component(modules = [CommonModule::class])
interface NoStrComponent {
    fun inject(foo :Foo)
}
```

하나의 컴포넌트는 String 의존성을 제공하는 HelloModule 을 추가하고 다른 하나의 컴포넌트는 추가 하지 않는다.

```kotlin
@Test
fun testFoo(){
		val foo = Foo()
		
		DaggerStrComponent.create().inject(foo)
		println(foo.str.isPresent())
		println(foo.str.get())
		
		DaggerNoStrComponent.create().inject(foo)
		println(foo.str.isPresent())
		println(foo.str.get())
}
```

```
결과
true
Hello
false
java.util.NoSuchElementException : No value preset
```

Optional 타입인 foo.str 이 String이 바인드 되었을 때는 present 상태이고 String 이 바인드되지 않았을 때는 absent인 것을 확인할 수 있으며, absent 상태일 때 get() 메서드로 값을 참조하면 에러가 발생하는 것도 확인할 수 있다.





### @BindsIntance

`@BindsIntance` 애노테이션은 컴포넌트빌더의 세터 메서드 또는 컴포넌트 팩토리의 매개변수에 붙일 수 있다.

모듈이 아닌 외부로부터 생성된 인스턴스를 빌더 또는 팩토리를 통해 넘겨줌으로써 컴포넌트가 해당 인스턴스를 바인드하게 된다.

이러한 인스턴스들은 모듈로부터 제공되는 인스턴스와 동일하게 `@Inject` 가 붙은 필드, 생성자, 메서드에 주입될 수 있다.

```kotlin
@Component
interface BindsComponent{
		fun inject(foo : Foo)
		@Component.Builder
		interface Builder{
				@BindsInstance
				fun setString(str : String) : Builder
				fun build() : BindsComponent
		}
}
```

```kotlin
class Foo{
		@Inject
		lateinit str : String
}
```

Builder를 만들고 `@BindsIntance`가 붙은 setString 세터 메서드를 추가 했다. 이 메서드에 외부로부터 생성한 String 객체를 바인드 한 것을 다음 유닛 테스트를 통해 확인 할 수 있다.

```kotlin
@Test
fun testBindsInstance(){
		val hello = "Hello World"
		val foo = Foo()
		val component = DaggetBindsComponent.builder()
																				.setString(hello)
																				.build()
		component.inject(foo)
		assertEquals("Hello World", foo.str)
}

/*
* 결과
* Hello World
*/
```



---