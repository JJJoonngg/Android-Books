package kr.co.jjjoonngg.dagger_sample_project.main

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.jjjoonngg.dagger_sample_project.App
import kr.co.jjjoonngg.dagger_sample_project.R
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

//    @Inject
//    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var activityName: String

    private lateinit var component: MainActivityComponent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        component = App().getAppComponent()
            .mainActivityComponentBuilder()
            .setModule(MainActivityModule())
            .setActivity(this)
            .build()

        component.inject(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment())
            .commit()
    }

    fun getComponent() = component
}