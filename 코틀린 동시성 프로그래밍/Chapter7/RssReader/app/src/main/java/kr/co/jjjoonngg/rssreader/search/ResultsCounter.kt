package kr.co.jjjoonngg.rssreader.search

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.newSingleThreadContext

/*
* Created by JJJoonngg
*/
enum class Action {
    INCREASE,
    RESET
}

@ObsoleteCoroutinesApi
object ResultsCounter {
    @ObsoleteCoroutinesApi
    private val context = newSingleThreadContext("counter")
    private var counter = 0

    private val notifications = Channel<Int>(Channel.CONFLATED)

    private val actor = GlobalScope.actor<Action>(context) {
        for (msg in channel) {
            when (msg) {
                Action.INCREASE -> counter++
                Action.RESET -> counter = 0
            }
            notifications.send(counter)
        }
    }

    suspend fun increment() = actor.send(Action.INCREASE)

    suspend fun reset() = actor.send(Action.RESET)

    fun getNotificationChannel(): ReceiveChannel<Int> = notifications
}