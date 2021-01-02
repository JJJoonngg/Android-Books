package kr.co.jjjoonngg.dagger_sample_project.di

import dagger.BindsInstance
import dagger.Component
import kr.co.jjjoonngg.dagger_sample_project.App
import kr.co.jjjoonngg.dagger_sample_project.main.MainActivityComponent
import javax.inject.Singleton

/*
* Created by JJJoonngg
*/

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun mainActivityComponentBuilder(): MainActivityComponent.Builder
    fun inject(app: App)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: App, appModule: AppModule): AppComponent
    }
}