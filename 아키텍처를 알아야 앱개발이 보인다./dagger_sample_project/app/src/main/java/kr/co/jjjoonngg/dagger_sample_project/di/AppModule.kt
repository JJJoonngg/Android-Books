package kr.co.jjjoonngg.dagger_sample_project.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import kr.co.jjjoonngg.dagger_sample_project.main.MainActivity
import kr.co.jjjoonngg.dagger_sample_project.main.MainActivityModule
import javax.inject.Named
import javax.inject.Singleton

/*
* Created by JJJoonngg
*/

@Module
abstract class AppModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivity(): MainActivity

    companion object {
        @Named("app")
        @Provides
        @Singleton
        fun provideString() = "String from AppModule"
    }
}