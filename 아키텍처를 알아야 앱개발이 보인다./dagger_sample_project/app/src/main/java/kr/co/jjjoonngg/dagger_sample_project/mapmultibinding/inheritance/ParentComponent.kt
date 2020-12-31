package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.inheritance

import dagger.Component

/*
* Created by JJJoonngg
*/

@Component(modules = [ParentModule::class])
interface ParentComponent {
    fun strings(): Set<String>
    fun childCompBuilder(): ChildComponent.Builder
}