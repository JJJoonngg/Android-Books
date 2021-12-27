package kr.co.jjjoonngg.applicationfortest

import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue


/*
* Created by JJJoonngg
*/

class SampleAppFT {

    @Test
    fun testHappyPath() = runBlocking {
        val manager = UserManager(MockDataSource())

        val user = manager.getUser(10)
        assertTrue { user.name == "Susan Calvin" }
        assertTrue { user.age == Calendar.getInstance().get(Calendar.YEAR) - 1982 }
        assertTrue { user.profession == "Robopsychologist" }
    }

    @Test
    fun testOppositeOrder() = runBlocking {
        val manager = UserManager(MockSlowDbDataSource())

        val user = manager.getUser(10)
        assertTrue { user.name == "Susan Calvin" }
        assertTrue { user.age == Calendar.getInstance().get(Calendar.YEAR) - 1982 }
        assertTrue { user.profession == "Robopsychologist" }

    }
}