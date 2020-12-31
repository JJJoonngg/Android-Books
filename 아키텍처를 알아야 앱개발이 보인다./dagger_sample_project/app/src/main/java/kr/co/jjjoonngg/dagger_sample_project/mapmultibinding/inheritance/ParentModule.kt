package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.inheritance

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/*
* Created by JJJoonngg
*/

@Module(subcomponents = [ChildComponent::class])
class ParentModule {
    @Provides
    @IntoSet
    fun string1() = "parent string 1"

    @Provides
    @IntoSet
    fun string2() = "parent string 2"
}