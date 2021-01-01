package kr.co.jjjoonngg.dagger_sample_project.subcomponentexample

import dagger.Subcomponent

/*
* Created by JJJoonngg
*/

@Subcomponent(modules = [MachineModule::class])
interface MachineComponent {

    fun getCoffee(): Coffee
    fun inject(machine: Machine)

    @Subcomponent.Builder
    interface Builder {
        fun setMachineModule(coffeeMachineModule: MachineModule): Builder
        fun build(): MachineComponent
    }
}
