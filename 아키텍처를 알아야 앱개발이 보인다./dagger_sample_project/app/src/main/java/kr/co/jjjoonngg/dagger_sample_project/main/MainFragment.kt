package kr.co.jjjoonngg.dagger_sample_project.main

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/*
* Created by JJJoonngg
*/

class MainFragment : Fragment() {

    @Inject
    @Named("app")
    lateinit var appString: String

    @Inject
    @Named("activity")
    lateinit var activityString: String

    @Inject
    @Named("fragment")
    lateinit var fragmentString: String

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        Log.e("MainFragment", appString)
        Log.e("MainFragment", activityString)
        Log.e("MainFragment", fragmentString)
        super.onAttach(context)
    }
}