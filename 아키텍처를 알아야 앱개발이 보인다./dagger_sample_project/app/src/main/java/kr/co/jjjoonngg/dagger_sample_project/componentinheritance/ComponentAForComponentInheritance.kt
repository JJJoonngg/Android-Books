package kr.co.jjjoonngg.dagger_sample_project.componentinheritance

import dagger.Component

/*
* Created by JJJoonngg
*/

@Component(modules = [ModuleAForComponentInheritance::class])
interface ComponentAForComponentInheritance {
    fun provideString(): String
}