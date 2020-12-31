package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.inheritance

import dagger.Subcomponent

/*
* Created by JJJoonngg
*/

@Subcomponent(modules = [ChildModule::class])
interface ChildComponent {
    fun strings(): Set<String>

    @Subcomponent.Builder
    interface Builder {
        fun build(): ChildComponent
    }
}