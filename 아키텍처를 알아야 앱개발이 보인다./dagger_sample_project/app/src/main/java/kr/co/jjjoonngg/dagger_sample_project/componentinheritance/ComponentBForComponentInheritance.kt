package kr.co.jjjoonngg.dagger_sample_project.componentinheritance

import dagger.Component
import kr.co.jjjoonngg.dagger_sample_project.setmultibinding.Foo

/*
* Created by JJJoonngg
*/

@Component(
    modules = [ModuleBForComponentInheritance::class],
    dependencies = [ComponentAForComponentInheritance::class]
)
interface ComponentBForComponentInheritance {
    fun inject(foo: FooForComponentInheritance)
}