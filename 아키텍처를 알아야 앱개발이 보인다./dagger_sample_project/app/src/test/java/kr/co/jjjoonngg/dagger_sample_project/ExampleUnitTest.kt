package kr.co.jjjoonngg.dagger_sample_project

import dagger.MembersInjector
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
}