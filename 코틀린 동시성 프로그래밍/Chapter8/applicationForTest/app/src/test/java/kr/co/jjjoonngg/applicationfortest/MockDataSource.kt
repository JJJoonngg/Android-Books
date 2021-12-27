package kr.co.jjjoonngg.applicationfortest

import kotlinx.coroutines.*
import java.util.*

/*
* Created by JJJoonngg
*/

class MockDataSource : DataSource {

    override fun getNameAsync(id: Int): Deferred<String> =
        CoroutineScope(Dispatchers.Default).async {
            delay(200)
            "Susan Calvin"
        }

    override fun getAgeAsync(id: Int): Deferred<Int> = CoroutineScope(Dispatchers.Default).async {
        delay(500)
        Calendar.getInstance().get(Calendar.YEAR) - 1982
    }

    override fun getProfessionAsync(id: Int): Deferred<String> =
        CoroutineScope(Dispatchers.Default).async {
            delay(2000)
            "Robopsychologist"
        }
}