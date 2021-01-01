package kr.co.jjjoonngg.dagger_sample_project.daggerbase

import dagger.Component
import dagger.MembersInjector

/*
* Created by JJJoonngg
*/

@Component(modules = [MyModule::class])
interface MyComponent {
    fun getString(): String

    fun getInt(): Int?

    fun inject(myClass: MyClass)

    fun getInjector(): MembersInjector<MyClass>
}