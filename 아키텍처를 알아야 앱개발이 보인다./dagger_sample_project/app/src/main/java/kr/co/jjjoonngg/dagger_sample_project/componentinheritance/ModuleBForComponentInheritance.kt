package kr.co.jjjoonngg.dagger_sample_project.componentinheritance

import dagger.Module
import dagger.Provides

/*
* Created by JJJoonngg
*/

@Module
class ModuleBForComponentInheritance {
    @Provides
    fun provideInteger(): Int = 100
}