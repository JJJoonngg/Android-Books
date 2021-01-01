## 1. 의존성 주입이란?

소프트웨어 공학에서 말하는 의존성 주입(DI, Dependency Injection)이란 하나의 객체에 다른 객체의 의존성을 제공하는 기술을 말한다.



의존성을 객체 지향에서 두 클래스 간의 관계이다. 일반적으로 둘중 하나가 다른 하나를 필요로 한다.

조립식 컴퓨터를 생각하며 다음 의존 관계에 대한 예제를 살펴본다. 

> 책의 예제는 java 이지만, 여기서는 kotlin 으로 예제를 표현한다.

```kotlin
class CPU{}

class Computer{
  private lateinit var cpu : CPU
  
  Computer(){
    cpu = new CPU()
  }
}
```

컴퓨터에 포함되는 CPU 가 컴퓨터가 생성되는 단계부터 단단히 결합한 모습을 보인다. 다른 CPU 로 업그레이드 하고 싶어도 변경할 수가 없다.

이를 "Computer가 CPU에 의존성을 갖는다." 라고 할 수 있다.



주입은 생성자나 메서드 등을 통해 외부로부터 생성된 객체를 전달받는 것을 의미한다.

```kotlin
class Computer{
  
  private lateinit var cpu : CPU
  
  fun setCPU(cpu : CPU){
    this.cpu = cpu    
  }
}
```

setCPU(CPU) 메서드를 통해 외부로부터 생성된 객체를 전달 받아 멤버 변수에 넣는다.



앞에서 설명한 의존성과 주입을 합쳐보면 "**의존 관계에 있는 클래스의 객체를 외부로부터 생성하여 주입받는다.**" 라고 말할 수 있다.



---





## 2. 의존성 주입의 필요성

의존성 주입의 필요성에 대해 알아보고 의존성 주입에 대한 이해도를 높여 본다.



### 변경의 전이

다시 한 번 컴퓨터와 CPU 의 관계를 확인해 본다. 'Computer' 는 'CPU' 라는 한가지 타입에 의존한다. 하지만 사용자는 다른 타입의 CPU를 사용하는 것을 원할 수 있다.

예를 들어 A사의 CPU로 컴퓨터를 조립하기를 원한다면 기존 CPU 클래스명을 A_CPU 로 변경하거나 새로 만들어야한다. 하나의 클래스를 변경하거나 새로 만드는 것은 어렵지 않다. 여기서 문제점은 CPU 클래스를 의존하던 Computer 클래스도 같이 변경해야 한다는 점이다.

```kotlin
class Computer{
  
  private lateinit var cpu : A_CPU //Changed
  
  Computer(){
    cpu = new A_CPU() //Changed
  }
}
```

하나의 클래스를 변경함으로써 다른 의존 관계까지 변경 사항이 전이된다.



이를 해결할 방법은 Computer가 의존하는 CPU를 interface로 만드는 것이다. CPU를 구현한 어떤 클래스 간에 Computer의 CPU 로 기능할 수 있다.

```kotlin
interface CPU {...}

class A_CPU : CPU {...}

class Computer{
  private lateinit var cpu : CPU
  
  Computer(){
    cpu = new A_CPU()
    //cpu = new I_CPU()
  }
}
```

하지만 여전히 문제점은 남아 있다. CPU를 인터페이스로 변경함에 따라 변경의 전이를 최소화했지만, Computer 클래스에서 CPU 객체를 생성하고 관리해 I 회사의 CPU 를 사용한다면 또 다시 Computer 클래스를 변경해야 한다.



### 제어의 역전 (IoC, Inversion of Control)

제어의 역전은 어떠한 일을 구행하도록 만들어진 프레임워크에 제어권을 위임함으로써 관심사를 분리하는 것을 의미한다. 제어의 역전을 통해 앞의 코드들의 문제점을 해결해본다.

```kotlin
class Computer{
  
  private lateinit var cpu : CPU
  
  Computer(){}
  
  Computer(cpu : CPU){
    this.cpu = cpu
  }
  
  fun setCPU(cpu : CPU){
    this.cpu = cpu
  }
} 

fun main{
  var cpu = new I_CPU
  var computer1 = new Computer(CPU)
  
 //OR
  
  var computer2 = new Comptuer()
  computer2.setCPU(cpu)
}
```

Computer 클래스의 생성자에서 CPU 객체를 만들지 않고, 외부로부터 CPU 객체를 생성한 뒤 Computer 생성자 또는 메서드의 매개 변수로 객체를 제공한다. 기존에는 Comptuer 가 CPU의 객체를 생성하고 관리했으나 개선된 코드에서는 CPU 객체의 생성및 관리를 외부에 위임했다. 이를 제어의 역전이라고 한다. 제어의 역전을 통해 결합도를 약하게 만들었고, Computer는 이제 CPU 의 변경 사항에 의해 내부 필드나 메서드 매개 변수를 변경하지 않아도 된다.



### 의존성 주입의 장단점

- **장점**
  - 인터페이스 기반으로 설계되며, 코드를 유연하게 한다.
  - 주입하는 코드만 따로 변경하기 쉬워 리팩토링이 수월하다.
  - **사용 결과로 stub 나 mock 객체를 사용하여 단위 테스트를 하기 더욱 쉬워진다.**
  - 클래스 간의 결합도를 느슨하게 한다.
  - 인터페이스를 기반으로 설계하므로 여러 개발자가 서로 사용하는 클래스를 독립적으로 개발할 수 있다. 즉, 클래스 간에 의존하는 인터페이스만 알면된다.



- **단점**
  - 간단한 프로그램을 만들 떄는 번거롭다.
  - 의존성 주입은 동작과 구성을 분리해 코드를 추거하기 어렵게 하고, 가독성을 떨어뜨릴 수 있다. 즉, 개발자는 더 많은 파일을 참조 해야만 한다.
  - Dagger2와 같은 DI Framework 는 컴파일 타임에 어노테이션 프로세서를 이용하여 파일을 생성하므로 빌드에 시간이 조금 더 소요된다.



짧은 기간 개발하고, 더는 유지 보수를 안 하는 간단한 프로그램을 만드는 경우에는 의존성 주입의 사용을 굳이 추천하지 않는다. 왜냐하면 의존성 주입을 하려고 인터페이스 기반으로 설계하고, 의존성 주입 프레이므 워크의 설정 등이 생산성을 떨어뜨리기 때문이다. 하지만 일반적인 상용 애플리케이션을 만들고, 지속해서 유지 보수를 할 경우에는 오히려 생산성을 향상한다.



의존성 주입을 사용하는 경우 코드 추적이 힘들어지고, 대표적 의존성 주입 도구인 Dagger2 사용 시 빌드 시간이 조금 더 소요된다는 단점도 있다. 하지만 적응하고 나면 코드 추적을 그렇게 어렵지 않고, Dagger2를 사용하여 다른 부분에서 크게 시간을 절약할 수 있어 오히려 Dagger2를 사용하지 않는 것 보다 사용하는 편이 시간을 더 절약할 수 있다.