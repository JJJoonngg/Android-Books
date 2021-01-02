package kr.co.jjjoonngg.dagger_sample_project.main

import dagger.Module
import dagger.Provides

/*
* Created by JJJoonngg
*/

@Module(subcomponents = [MainFragmentComponent::class])
class MainActivityModule {
    @Provides
    fun provideActivityName() = MainActivity::class.simpleName ?: "Failed get Activity Simple Name"

}