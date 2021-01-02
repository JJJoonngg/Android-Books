package kr.co.jjjoonngg.dagger_sample_project.main

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Named

/*
* Created by JJJoonngg
*/

@Module(subcomponents = [MainFragmentSubcomponent::class])
abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ClassKey(MainFragment::class)
    abstract fun bindInjectorFactory(factory: MainFragmentSubcomponent.Factory): AndroidInjector.Factory<*>

    companion object {
        @Named("activity")
        @Provides
        fun provideString() = "String from MainActivityModule"
    }
}