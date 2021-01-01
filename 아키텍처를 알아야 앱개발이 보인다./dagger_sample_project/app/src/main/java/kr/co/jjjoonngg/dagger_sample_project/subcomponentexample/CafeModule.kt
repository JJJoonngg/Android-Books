package kr.co.jjjoonngg.dagger_sample_project.subcomponentexample

import dagger.Module
import dagger.Provides

/*
* Created by JJJoonngg
*/

@Module(subcomponents = [MachineComponent::class])
class CafeModule {

    @Provides
    fun provideWater() = Water()

    @Provides
    fun provideMachine(builder: MachineComponent.Builder) = Machine(builder)
}