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

@Module
class MainFragmentModule {
    @Provides
    fun provideInt() = Random().nextInt()
}

class MainFragment : Fragment() {
//    @Inject
//    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var activityName: String

    @set: [Inject Named("int")]
    var randomNumber: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is MainActivity) {
            (activity as MainActivity).getComponent()
                .mainFragmentComponentBuilder()
                .setModule(MainFragmentModule())
                .setFragment(this)
                .build()
                .inject(this)
        }

        Log.d("MainFragment", activityName)
        Log.d("MainFragment", "randomNumber = $randomNumber")
    }
}