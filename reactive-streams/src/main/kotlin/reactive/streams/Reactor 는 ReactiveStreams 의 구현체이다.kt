package org.example.reactive.streams

import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

fun main(){
    val executor = Executors.newSingleThreadExecutor()
    val scheduler = Schedulers.fromExecutor(executor)

    println("[${Thread.currentThread().name}] main thread start...")
    val simpledSubscriber = SimpleSubscriber()
    Flux.just(1, 2, 3, 4, 5)
        .subscribeOn(scheduler)
        .doFinally {
            executor.shutdown()
        }
        .subscribe(
            simpledSubscriber
        )
}