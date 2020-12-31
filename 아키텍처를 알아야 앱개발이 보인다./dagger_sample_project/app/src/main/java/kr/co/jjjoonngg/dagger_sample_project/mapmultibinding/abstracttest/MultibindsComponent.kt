package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.abstracttest

import dagger.Component

/*
* Created by JJJoonngg
*/

@Component(modules = [MultibindsModules::class])
interface MultibindsComponent {
    fun getStrings(): Set<String>
}