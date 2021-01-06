package kr.co.jjjoonngg.dagger_sample_project

import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.reactivestreams.Publisher
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/*
* Created by JJJoonngg
*/

class ObservableCreateTest {
    
    @Test
    fun observableOperator() {
        val source = Observable.create<String> { emitter ->
            emitter.onNext("Hello")
            emitter.onError(Throwable())
            emitter.onNext("World")
        }

        source.subscribe(
            (System.out::println),
            { _ -> println("Error!!") })

        val source2 = Observable.just("Hello", "World")
        source2.subscribe(System.out::println)
        /* 결과
        Hello
        World
         */

        val itemArray = arrayOf("A", "B", "C")
        val source3 = Observable.fromArray(itemArray)
        source3.subscribe { items ->
            for (item in items) {
                println(item)
            }
        }
        /* 결과
        A
        B
        C
         */


        val itemList = arrayListOf("A", "B", "C")
        val source4 = Observable.fromIterable(itemList)
        source4.subscribe(System.out::println)
        /*결과
        A
        B
        C
         */

        val future = Executors.newSingleThreadExecutor()
            .submit<String> {
                Thread.sleep(500)
                "Hello World"
            }
        Observable.fromFuture(future).apply {
            subscribe(System.out::println)
        }
        /*
        결과
        Hello World
        (5초 후 나타남)
         */

        val publisher = Publisher<String> {
            it.onNext("A")
            it.onNext("B")
            it.onNext("C")
            it.onComplete()
        }
        Observable.fromPublisher(publisher).apply {
            subscribe(System.out::println)
        }
        /*
        결과
        A
        B
        C
         */

        val callable = Callable(){
            return@Callable "Hello World"
        }
        Observable.fromCallable(callable).apply {
            subscribe(System.out::println)
        }
        /*
        결과
        Hello World
         */
    }
}