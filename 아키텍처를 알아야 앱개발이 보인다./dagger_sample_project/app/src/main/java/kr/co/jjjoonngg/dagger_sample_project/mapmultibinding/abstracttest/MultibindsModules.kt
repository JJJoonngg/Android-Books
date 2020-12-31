package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding.abstracttest

import dagger.Module
import dagger.multibindings.Multibinds

/*
* Created by JJJoonngg
*/

@Module
abstract class MultibindsModules {
    @Multibinds
    abstract fun strings(): Set<String>
}