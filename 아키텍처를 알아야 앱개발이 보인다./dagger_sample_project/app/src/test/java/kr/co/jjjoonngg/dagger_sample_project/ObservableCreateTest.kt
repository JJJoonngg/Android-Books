package kr.co.jjjoonngg.dagger_sample_project

import io.reactivex.rxjava3.core.*
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

        val callable = Callable() {
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

    @Test
    fun singleTest() {
        Single.just("Hello World")
            .subscribe(System.out::println)

        Single.create { emitter: SingleEmitter<Any?> -> emitter.onSuccess("Hello") }
            .subscribe(System.out::println)
    }

    @Test
    fun maybeTest() {
        Maybe.create { emitter: MaybeEmitter<Any?> ->
            emitter.onSuccess(100)
            emitter.onComplete() // 무시됨
        }.doOnSuccess { item: Any? -> println("doOnSuccess1") }
            .doOnComplete { println("doOnComplete1") }
            .subscribe(System.out::println)

        Maybe.create { emitter: MaybeEmitter<Any?> ->
            emitter.onComplete()
        }.doOnSuccess { item: Any? -> println("doOnSuccess2") }
            .doOnComplete { println("doOnComplete2") }
            .subscribe(System.out::println)
        /*결과
        doOnSuccess1
        100
        doOnComplete2
        */

        val src1 = Observable.just(1, 2, 3)
        val srcMaybe1 = src1.firstElement()
        srcMaybe1.subscribe(System.out::println)

        val src2 = Observable.empty<Int>()
        val srcMaybe2 = src2.firstElement()
        srcMaybe2.subscribe(
            { x: Int? -> println(x) },
            { throwable: Throwable? -> })
        { println("onComplete!") }
        /*결과
        1
        onComplete!
        */
    }

    @Test
    fun completableTest() {
        Completable.create { emitter ->
            //do something here
            emitter.onComplete()
        }.subscribe { println("completed1") }

        Completable.fromRunnable {
            //do something here
        }.subscribe { println("completed2") }
        /*
        결과
        completed1
        completed2
        */
    }
}