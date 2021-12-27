package kr.co.jjjoonngg.applicationfortest

import kotlinx.coroutines.Deferred

/*
* Created by JJJoonngg
*/
interface DataSource {
    fun getNameAsync(id: Int): Deferred<String>
    fun getAgeAsync(id: Int): Deferred<Int>
    fun getProfessionAsync(id: Int): Deferred<String>
}