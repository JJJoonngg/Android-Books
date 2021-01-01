package kr.co.jjjoonngg.dagger_sample_project.subcomponentexample

import dagger.Component

/*
* Created by JJJoonngg
*/

@Component(modules = [CafeModule::class])
interface CafeComponent {
    fun inject(cafe: Cafe)
}