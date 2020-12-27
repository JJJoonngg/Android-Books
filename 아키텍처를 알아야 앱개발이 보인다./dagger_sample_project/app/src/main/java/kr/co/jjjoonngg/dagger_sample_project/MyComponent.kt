package kr.co.jjjoonngg.dagger_sample_project

import dagger.Component

/*
* Created by JJJoonngg
*/

@Component(modules = [MyModule::class])
interface MyComponent {
    fun getString(): String
}