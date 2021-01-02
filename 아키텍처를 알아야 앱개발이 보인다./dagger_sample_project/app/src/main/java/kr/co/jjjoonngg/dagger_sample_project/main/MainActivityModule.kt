package kr.co.jjjoonngg.dagger_sample_project.main

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Named

/*
* Created by JJJoonngg
*/

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [MainFragmentModule::class])
    abstract fun mainFragment(): MainFragment

    companion object {
        @Named("activity")
        @Provides
        fun provideString() = "String from MainActivityModule"
    }
}