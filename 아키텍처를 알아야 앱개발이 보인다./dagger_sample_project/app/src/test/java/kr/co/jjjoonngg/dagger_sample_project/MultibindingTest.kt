package kr.co.jjjoonngg.dagger_sample_project

import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.inheritance.DaggerParentComponent
import kr.co.jjjoonngg.dagger_sample_project.setmultibinding.DaggerSetComponent
import kr.co.jjjoonngg.dagger_sample_project.setmultibinding.Foo
import org.junit.Test

/*
* Created by JJJoonngg
*/

class MultibindingTest {
    @Test
    fun testMultibindingSet() {
        val foo = Foo()
        DaggerSetComponent.create().inject(foo)
        foo.print()
    }

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
}