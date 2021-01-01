package kr.co.jjjoonngg.dagger_sample_project.subcomponentexample

/*
* Created by JJJoonngg
*/

class Machine(builder: MachineComponent.Builder) {

    private val component: MachineComponent = builder.setMachineModule(MachineModule()).build()

    init {
        component.inject(this)
    }

    fun extract() = component.getCoffee()
}