package kr.co.jjjoonngg.dagger_sample_project

import kr.co.jjjoonngg.dagger_sample_project.componentinheritance.*
import org.junit.Test

/*
* Created by JJJoonngg
*/

class ComponentInheritanceUnitTest {

    @Test
    fun testDependency() {
        val foo = FooForComponentInheritance()
        val componentA = DaggerComponentAForComponentInheritance.create()
        val componentB = DaggerComponentBForComponentInheritance.builder()
            .componentAForComponentInheritance(componentA)
            .build()
        componentB.inject(foo)
        println(foo.str)
        println(foo.int)
    }
    /*
    결과
    String from ModuleA
    100
     */

}