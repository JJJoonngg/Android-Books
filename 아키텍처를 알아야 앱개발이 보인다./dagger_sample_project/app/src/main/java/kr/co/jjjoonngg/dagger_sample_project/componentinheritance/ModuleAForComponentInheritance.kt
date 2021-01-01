package kr.co.jjjoonngg.dagger_sample_project.componentinheritance

import dagger.Module
import dagger.Provides

/*
* Created by JJJoonngg
*/

@Module
class ModuleAForComponentInheritance {
    @Provides
    fun provideString() = "String from ModuleA"
}