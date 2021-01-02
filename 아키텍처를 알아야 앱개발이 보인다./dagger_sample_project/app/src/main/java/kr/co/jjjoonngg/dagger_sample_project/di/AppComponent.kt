package kr.co.jjjoonngg.dagger_sample_project.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import kr.co.jjjoonngg.dagger_sample_project.App
import javax.inject.Singleton

/*
* Created by JJJoonngg
*/

@Singleton
@Component(modules = [AppModule::class, AndroidInjectionModule::class])
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<App> {
    }
}
