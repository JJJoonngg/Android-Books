package kr.co.jjjoonngg.dagger_sample_project.setmultibinding

import dagger.Component

/*
* Created by JJJoonngg
*/

@Component(modules = [SetModule::class])
interface SetComponent {
    fun inject(foo: Foo)
}