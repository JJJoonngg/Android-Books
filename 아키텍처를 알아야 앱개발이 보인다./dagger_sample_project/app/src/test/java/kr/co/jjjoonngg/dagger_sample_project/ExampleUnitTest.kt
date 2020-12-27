package kr.co.jjjoonngg.dagger_sample_project

import org.junit.Test

import org.junit.Assert.*

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
    }
}