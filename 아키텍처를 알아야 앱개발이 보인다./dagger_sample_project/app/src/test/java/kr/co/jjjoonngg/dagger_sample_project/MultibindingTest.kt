package kr.co.jjjoonngg.dagger_sample_project

import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.abstracttest.DaggerMultibindsComponent
import kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.abstracttest.MultibindsComponent
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

    @Test
    fun testMultibinds() {
        val component: MultibindsComponent = DaggerMultibindsComponent.create()

        //empty
        for (s in component.getStrings()) {
            println(s)
        }
    }
}