package kr.co.jjjoonngg.dagger_sample_project

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kr.co.jjjoonngg.dagger_sample_project.di.DaggerAppComponent
import javax.inject.Inject

/*
* Created by JJJoonngg
*/

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}