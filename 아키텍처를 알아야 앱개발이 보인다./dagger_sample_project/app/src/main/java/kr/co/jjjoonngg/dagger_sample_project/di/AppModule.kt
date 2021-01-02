package kr.co.jjjoonngg.dagger_sample_project.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import kr.co.jjjoonngg.dagger_sample_project.main.MainActivity
import kr.co.jjjoonngg.dagger_sample_project.main.MainActivitySubcomponent
import javax.inject.Named
import javax.inject.Singleton

/*
* Created by JJJoonngg
*/

@Module(subcomponents = [MainActivitySubcomponent::class])
abstract class AppModule {
    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    abstract fun bindAndroidInjectorFactory(factory: MainActivitySubcomponent.Factory): AndroidInjector.Factory<*>

    companion object {
        @Named("app")
        @Provides
        @Singleton
        fun provideString() = "String from AppModule"
    }
}