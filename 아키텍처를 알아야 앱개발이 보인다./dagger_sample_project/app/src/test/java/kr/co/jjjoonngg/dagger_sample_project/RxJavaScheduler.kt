package kr.co.jjjoonngg.dagger_sample_project

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.TimeUnit

/*
* Created by JJJoonngg
*/

class RxJavaScheduler {

    @Test
    fun function1() {
        val io = Schedulers.io()
        val computation = Schedulers.computation()
        val trampoline = Schedulers.trampoline()
        val newThread = Schedulers.newThread()
        val mainThread = AndroidSchedulers.mainThread()
    }

    @Test
    fun subscribeAndObserveOn() {
        Observable.create<Int> { emitter ->
            for (i in 0..3) {
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName $i")
                emitter.onNext(i)
                Thread.sleep(100)
            }
            emitter.onComplete()
        }.apply {
            subscribe { s ->
                val threadName = Thread.currentThread().name
                println("#Obsv on $threadName $s")
            }
        }
        /* Result
        #Subs on main 0
        #Obsv on main 0
        #Subs on main 1
        #Obsv on main 1
        #Subs on main 2
        #Obsv on main 2
        #Subs on main 3
        #Obsv on main 3
         */

        Observable.create<Int> { emitter ->
            for (i in 0..2) {
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName $i")
                emitter.onNext(i)
                Thread.sleep(100)
            }
            emitter.onComplete()
        }.apply {
            subscribeOn(Schedulers.io()).subscribe() { s ->
                val threadName = Thread.currentThread().name
                println("#Obsv on $threadName $s")
            }
            Thread.sleep(500)
        }
        /* Result
        #Subs on RxCachedThreadScheduler-1 0
        #Obsv on RxCachedThreadScheduler-1 0
        #Subs on RxCachedThreadScheduler-1 1
        #Obsv on RxCachedThreadScheduler-1 1
        #Subs on RxCachedThreadScheduler-1 2
        #Obsv on RxCachedThreadScheduler-1 2
         */


        Observable.create<Int> { emitter ->
            for (i in 0..2) {
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName $i")
                emitter.onNext(i)
                Thread.sleep(100)
            }
            emitter.onComplete()
        }.apply {
            observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe() { s ->
                    val threadName = Thread.currentThread().name
                    println("#Obsv on $threadName $s")
                }
            Thread.sleep(500)
        }

        /* Result
        #Subs on RxCachedThreadScheduler-1 0
        #Obsv on RxComputationThreadPool-1 0
        #Subs on RxCachedThreadScheduler-1 1
        #Obsv on RxComputationThreadPool-1 1
        #Subs on RxCachedThreadScheduler-1 2
        #Obsv on RxComputationThreadPool-1 2
         */

        Observable.interval(200, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe { value -> println("${Thread.currentThread().name} : $value") }
        Thread.sleep(1000)
        /* Result
        RxComputationThreadPool-2 : 0
        RxComputationThreadPool-2 : 1
        RxComputationThreadPool-2 : 2
        RxComputationThreadPool-2 : 3
        RxComputationThreadPool-2 : 4
         */
    }


}