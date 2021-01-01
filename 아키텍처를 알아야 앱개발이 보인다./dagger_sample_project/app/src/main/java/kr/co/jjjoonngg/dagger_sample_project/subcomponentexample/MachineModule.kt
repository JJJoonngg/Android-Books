package kr.co.jjjoonngg.dagger_sample_project.subcomponentexample

import dagger.Module
import dagger.Provides

/*
* Created by JJJoonngg
*/

@Module
class MachineModule {
    @Provides
    fun provideCoffeeBean() = CoffeeBean()
}