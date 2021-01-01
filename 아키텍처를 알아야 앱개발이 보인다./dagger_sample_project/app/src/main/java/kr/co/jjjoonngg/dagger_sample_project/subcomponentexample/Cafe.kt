package kr.co.jjjoonngg.dagger_sample_project.subcomponentexample

import javax.inject.Inject

/*
* Created by JJJoonngg
*/

class Cafe {
    @Inject
    lateinit var coffeeMachine: Machine

    init {
        DaggerCafeComponent.create().inject(this)
    }

    fun orderCoffee() = coffeeMachine.extract()
}