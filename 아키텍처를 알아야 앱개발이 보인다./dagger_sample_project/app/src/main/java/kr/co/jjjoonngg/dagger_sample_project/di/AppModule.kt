package kr.co.jjjoonngg.dagger_sample_project.di

import android.content.Context
import dagger.Module
import dagger.Provides
import kr.co.jjjoonngg.dagger_sample_project.App
import kr.co.jjjoonngg.dagger_sample_project.main.MainActivityComponent
import javax.inject.Singleton

/*
* Created by JJJoonngg
*/

@Module(subcomponents = [MainActivityComponent::class])
class AppModule {

//    @Provides
//    @Singleton
//    fun provideSharedPreferences(app: App) =
//        app.getSharedPreferences("default", Context.MODE_PRIVATE)
}