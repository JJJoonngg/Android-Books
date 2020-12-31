package kr.co.jjjoonngg.dagger_sample_project.setmultibinding

import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.IntoSet

/*
* Created by JJJoonngg
*/


@Module
class SetModule {
    @Provides
    @IntoSet
    fun provideHello() = "Hello"

    @Provides
    @IntoSet
    fun provideWorld() = "World"

    @Provides
    @ElementsIntoSet
    fun provideSet(): Set<String> = HashSet<String>(listOf("Charles", "Runa"))
}