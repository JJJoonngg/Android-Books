package kr.co.jjjoonngg.dagger_sample_project.main

import dagger.Subcomponent
import dagger.android.AndroidInjector

/*
* Created by JJJoonngg
*/

@Subcomponent(modules = [MainActivityModule::class])
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainActivity> {}
}