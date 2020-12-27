package kr.co.jjjoonngg.dagger_sample_project

import dagger.Module
import dagger.Provides

/*
* Created by JJJoonngg
*/

@Module
class MyModule {

    @Provides
    fun provideHelloWorld(): String = "Hello World"

}