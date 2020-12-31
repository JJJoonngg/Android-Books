package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.inheritance

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/*
* Created by JJJoonngg
*/

@Module
class ChildModule {
    @Provides
    @IntoSet
    fun string3() = "child string 1"

    @Provides
    @IntoSet
    fun string4() = "child string 2"
}