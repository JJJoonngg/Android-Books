package kr.co.jjjoonngg.dagger_sample_project

import dagger.MembersInjector
import io.reactivex.rxjava3.subjects.PublishSubject
import kr.co.jjjoonngg.dagger_sample_project.daggerbase.DaggerMyComponent
import kr.co.jjjoonngg.dagger_sample_project.daggerbase.MyClass
import kr.co.jjjoonngg.dagger_sample_project.daggerbase.MyComponent
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testHelloWorld() {
        val myComponent: MyComponent = DaggerMyComponent.create()
        println("result = ${myComponent.getString()}")

        println("nullable check = ${myComponent.getInt()}")
    }

    @Test
    fun testMemberInjection() {
        val myClass = MyClass()
        var str = myClass.str
        println("조회 결과 $str")
        val myComponent = DaggerMyComponent.create()
        myComponent.inject(myClass)
        str = myClass.str
        println("$str")
    }

    @Test
    fun testMemberInjector() {
        val myClass = MyClass()
        var str = myClass.str
        println("result = $str")
        val myComponent = DaggerMyComponent.create()
        val injector: MembersInjector<MyClass> = myComponent.getInjector()
        injector.injectMembers(myClass)
        str = myClass.str
        println("result = $str")
    }

    @Test
    fun imperativeProgramming() {
        val items = ArrayList<Int>()
        items.add(1)
        items.add(2)
        items.add(3)
        items.add(4)

        //Print Even Number
        for (item in items) {
            if (item % 2 == 0) {
                println(item)
            }
        }

        /*
        결과
        2
        4
        */

        items.add(5)
        items.add(6)
        items.add(7)
        items.add(8)
    }

    @Test
    fun reactiveProgramming() {
        val items: PublishSubject<Int> = PublishSubject.create()
        items.onNext(1)
        items.onNext(2)
        items.onNext(3)
        items.onNext(4)
        //Print Even Number
        items.filter { item -> item % 2 == 0 }
            .subscribe(System.out::println)

        items.onNext(5)
        items.onNext(6)
        items.onNext(7)
        items.onNext(8)
        /*
        결과
        6
        8
         */
    }
}