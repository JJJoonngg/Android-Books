package kr.co.jjjoonngg.dagger_sample_project.main

import dagger.BindsInstance
import dagger.Subcomponent

/*
* Created by JJJoonngg
*/

@Subcomponent(modules = [MainFragmentModule::class])
interface MainFragmentComponent {

    fun inject(mainFragment: MainFragment)

    @Subcomponent.Builder
    interface Builder {
        fun setModule(module: MainFragmentModule): MainFragmentComponent.Builder

        @BindsInstance
        fun setFragment(fragment: MainFragment): MainFragmentComponent.Builder
        fun build(): MainFragmentComponent
    }
}