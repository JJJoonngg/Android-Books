package kr.co.jjjoonngg.dagger_sample_project.mapmultibinding

import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

/*
* Created by JJJoonngg
*/

@Module
class MapModule {

    @Provides
    @IntoMap
    @StringKey("foo")
    fun provideFooValue() = 100L


    @Provides
    @IntoMap
    @ClassKey(FooForMapMultibinding::class)
    fun provideFooStr() = "Foo String"
}