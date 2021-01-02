package kr.co.jjjoonngg.dagger_sample_project.main

import dagger.Module
import dagger.Provides
import javax.inject.Named

/*
* Created by JJJoonngg
*/

@Module
class MainFragmentModule {

    @Named("fragment")
    @Provides
    fun provideString() = "String from fragment"
}