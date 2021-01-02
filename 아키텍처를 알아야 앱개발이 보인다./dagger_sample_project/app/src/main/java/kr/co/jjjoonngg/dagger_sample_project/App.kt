package kr.co.jjjoonngg.dagger_sample_project

import android.app.Application
import kr.co.jjjoonngg.dagger_sample_project.di.AppComponent
import kr.co.jjjoonngg.dagger_sample_project.di.AppModule
import kr.co.jjjoonngg.dagger_sample_project.di.DaggerAppComponent

/*
* Created by JJJoonngg
*/

class App : Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory()
            .create(this, AppModule())
    }

    fun getAppComponent() = if (::appComponent.isInitialized) {
        appComponent
    } else {
        DaggerAppComponent.factory()
            .create(this, AppModule())
    }
}