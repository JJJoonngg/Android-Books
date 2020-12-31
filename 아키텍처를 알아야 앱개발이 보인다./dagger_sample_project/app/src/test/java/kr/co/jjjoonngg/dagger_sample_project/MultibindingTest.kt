package kr.co.jjjoonngg.dagger_sample_project

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
}