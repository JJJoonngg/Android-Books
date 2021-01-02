package kr.co.jjjoonngg.dagger_sample_project.main

import dagger.Subcomponent
import dagger.android.AndroidInjector

/*
* Created by JJJoonngg
*/

@Subcomponent(modules = [MainFragmentModule::class])
interface MainFragmentSubcomponent : AndroidInjector<MainFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainFragment> {}
}