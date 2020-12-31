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



## 12.멀티 바인딩 하기

Dagger의 멀티 바인딩을 사용하여 여러 모듈에 있는 같은 타입의 객체를 하나의 Set 또는 Map 형태로 관리할 수 있다.





### Set 멀티 바인딩

Set으로 멀티 바인딩을 구현하려면 `@IntoSet` 과 `@ElementsIntoSet` 애노테이션을 `@Provides` 메서드와 함께 사용할 수 있다. 

```kotlin
@Module
class SetModule {
    @Provides
    @IntoSet
    fun provideHello() = "Hello"

    @Provides
    @IntoSet
    fun provideWorld() = "World"
}
```

간단히 `@IntoSet` 을 사용하는 것으로 `Set<String>` 타입으로 멀티 바인딩이 구현된다.

객체를 하나씩 Set에 추가하는 것이 아니라 `Set<String>` 의 일부분을 한꺼번에 추가할 수 있는데 , 

이때는 `@ElementsIntoSet` 애노테이션을 사용한다.

```kotlin
@Module
class SetModule {
 		...
    @Provides
    @ElementsIntoSet
    fun provideSet(): Set<String> = HashSet<String>(listOf("Charles", "Runa"))
}
```



Set 으로 멀티 바인딩이 잘 구현되었는지 확인하려 다음과 같이 SetComponent와 멀티 바인드된 의존성을 주입하는 Foo 클래스를 만든다.

```kotlin
@Component(modules = [SetModule::class])
interface SetComponent {
    fun inject(foo: Foo)
}
```

```kotlin
class Foo {

    @Inject
    lateinit var strings: Set<String>

    fun print() {
        for (itr in strings.iterator()) {
            println(itr)
        }
    }
}
```

```kotlin
class MultibindingTest {
    @Test
    fun testMultibindingSet() {
        val foo = Foo()
        DaggerSetComponent.create().inject(foo)
        foo.print()
    }
}
```

```
결과
Charles
Runa
Hello
World
```



### Map 멀티 바인딩

map으로 멀티 바인딩을 구현하려면 모듈 내의 `@Provides` 메서드에 `@IntoMap` 을 추가해야 한다. 주의해야 할 점은 Map 을 사용하는 데는 키가 필요해 `@IntoMap`  애노테이션과 함께 별도의 키 애노테이션을 추가해야 한다.



#### 기본 제공하는 키의 종류

map을 위한 Dagger에서 기본으로 제공하는 키로는 `@StringKey` `@ClassKey` `@IntKey` `@LongKey` 가 있다

```kotlin
class FooForMapMultibinding {}
```

```kotlin
@Module
class MapModule {

    @Provides
    @IntoMap
    @StringKey("foo")
    fun provideFooValue() = 100L


    @Provides
    @IntoMap
    @ClassKey(FooForMapMultibinding::class)
    fun provideFooStr() = "Foo String"
}
```

```kotlin
@Component(modules = [MapModule::class])
interface MapComponent {
    fun getLongByString(): Map<String, Long>
    fun getStringByClass(): Map<Class<*>, String>
}
```

```kotlin
class MultibindingMapTest {
    @Test
    fun testMultibindingMap() {
        val component = DaggerMapComponent.create()
        val value = component.getLongByString()["foo"]
        val str = component.getStringByClass()[FooForMapMultibinding::class.java]

        println(value)
        println(str)
    }
}
```

```
결과
100
Foo String
```

모듈 내의 `@Provides` 메서드에 붙은 키가 Map의 키가 되고 메서드를 통해 반환되는 값을 통해 한쌍의 키-값을 이루는 것을 확인 할 수 있다.



#### 사용자 정의 키 만들기

직접 키를 정의할 수도 있다. 다음은 `@MapKey` 애노테이션을 통해 사용자 정의 키를 선언한 예제이다.

```kotlin
enum class Animal {
    CAT,
    DOG
}

@MapKey
annotation class AnimalKey(val value: Animal)

@MapKey
annotation class NumberKey(val value: KClass<out Number>)
```

AnimalKey 와 NumberKey 두가지를 이용한다.

```kotlin
@Component(modules = [MapModuleForCustomKey::class])
interface MapKeyComponentForCustomKey {
    fun getStringByAnimal(): Map<Animal, String>
    fun getStringByNUmber(): Map<Class<out Number>, String>
}
```

```kotlin
@Module
class MapModuleForCustomKey {

    @IntoMap
    @AnimalKey(Animal.CAT)
    @Provides
    fun provideCat() = "Meow"

    @IntoMap
    @AnimalKey(Animal.DOG)
    @Provides
    fun provideDog() = "Bow-wow"

    @IntoMap
    @NumberKey(Float::class)
    @Provides
    fun provideFloatValue() = "100f"


    @IntoMap
    @NumberKey(Int::class)
    @Provides
    fun provideIntegerValue() = "1"

}
```

```kotlin
@Test
fun testCustomKey() {
		val component = DaggerMapKeyComponentForCustomKey.create()
      
		println(component.getStringByAnimal()[Animal.CAT])
    println(component.getStringByAnimal()[Animal.DOG])
    println(component.getStringByNUmber()[Float::class.java])
    println(component.getStringByNUmber()[Int::class.java])
}
```

```
결과
Meow
Bow-wow
100f
1
```





#### 상속된 서브 컴포넌트의 멀티 바인딩

컴포넌트로 부터 멀티 바인드된 Set 또는 Map 을 서브 컴포넌트도 그대로 물려받을 수 있다.

```kotlin
@Subcomponent(modules = [ChildModule::class])
interface ChildComponent {
    fun strings(): Set<String>

    @Subcomponent.Builder
    interface Builder {
        fun build(): ChildComponent
    }
}
```

```kotlin
@Module
class ChildModule {
    @Provides
    @IntoSet
    fun string3() = "child string 1"

    @Provides
    @IntoSet
    fun string4() = "child string 2"
}
```

```kotlin
@Component(modules = [ParentModule::class])
interface ParentComponent {
    fun strings(): Set<String>
    fun childCompBuilder(): ChildComponent.Builder
}
```

```kotlin
@Module(subcomponents = [ChildComponent::class])
class ParentModule {
    @Provides
    @IntoSet
    fun string1() = "parent string 1"

    @Provides
    @IntoSet
    fun string2() = "parent string 2"
}
```

```kotlin
    @Test
    fun testMultibindingWithSubComponent() {
        val parentComp = DaggerParentComponent.create()
        val childComp = parentComp.childCompBuilder().build()

        println("List set in Parent")

        var itr = parentComp.strings().iterator()
        while (itr.hasNext()) {
            println(itr.next())
        }

        println("List set in child")
        itr = childComp.strings().iterator()
        while (itr.hasNext()) {
            println(itr.next())
        }

    }
```

```
결과
List set in Parent
parent string 1
parent string 2
List set in child
child string 2
child string 1
parent string 1
parent string 2
```



#### 추상적인 멀티 바인딩 선언하기

컴포넌트는 어려 모듈을 사용할 수 있어 다른 모듈에 의해 멀티 바인드를 사용 할 수도 있고, 사용하지 않을 수도 있다.

멀티 바인딩 사용 여부와 관계없이 멀티 바인드를 컴포넌트 내에서 지원하는 `@Multibinds` 애노테이션을 사용한 멀티 바인딩을 선언할 수 있다.



멀티 바인딩의 선언은`@Multibinds` 애노테이션을 사용한다. `@Multibinds` 애노테이션은 모듈 내의 매개 변수를 갖지 않는 추상 메서드에 사용할 수 있으며, 이 메서드는 반환 타입이 Map 또는 Set 이여야한다.

```kotlin
@Module
abstract class MultibindsModules {
    @Multibinds
    abstract fun strings(): Set<String>
}
```

```kotlin
@Component(modules = [MultibindsModules::class])
interface MultibindsComponent {
    fun getStrings(): Set<String>
}
```

```kotlin
@Test
fun testMultibinds() {
    val component: MultibindsComponent = DaggerMultibindsComponent.create()

    //empty
    for (s in component.getStrings()) {
        println(s)
    }
}
```

결과는 아무것도 나오지 않는다.



외부로부터 멀티 바인딩된 객체가 컴포넌트의 의존성으로 추가 된다면 Set을 조회했을 때 무언가 출력되는 것을 확인할 수도 있다. 비어 있는 Set을 멀티 바인딩한 효과와 같아 다음과 같이 `@Multibinds` 애노테이션을 사용하지 않고 `@ElementsIntoSet` 만을 사용 해서 멀티 바인딩을 선언할 수도 있다.

```kotlin
@Module
abstract class MultibindsModules {
    @Provides
    @ElementsIntoSet
    fun emptyStrings() = return Collections.emptySet()
}
```



---

