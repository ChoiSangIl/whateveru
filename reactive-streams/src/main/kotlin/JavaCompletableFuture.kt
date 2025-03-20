package org.example


import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.RejectedExecutionException

fun getFuture(): Future<String> {
    val executor = Executors.newSingleThreadExecutor()
    return try {
         executor.submit<String> {
             println("getFuture 실행됨")
             Thread.sleep(100)
             "Hello, World!"
        }
    } finally {
        executor.shutdown()
    }
}

fun getFutureAfter1s(): Future<String> {
    val executor = Executors.newSingleThreadExecutor()
    return try {
        executor.submit<String> {
            Thread.sleep(1000)
            "Hello, World!"
        }
    } finally {
        executor.shutdown()
    }
}

fun getFutureException(): Future<String> {
    val executor = Executors.newSingleThreadExecutor()
    return try {
        executor.submit<String> {
            throw IllegalArgumentException("Error")
        }
    } finally {
        executor.shutdown()
    }
}

fun main(){
    val future1: Future<String> = getFutureAfter1s()
    val future2: Future<String> = getFutureAfter1s()
    val future3: Future<String> = getFutureAfter1s()

    // future 의 상태 조회
    future1.cancel(true)
    println("future-isDone : ${future1.isDone} future-isCancelled : ${future1.isCancelled}")
    println("future-isDone : ${future2.isDone} future-isCancelled : ${future2.isCancelled}")
    println("future-isDone : ${future3.isDone} future-isCancelled : ${future3.isCancelled}")

    val futureException = getFutureException()
    println("futureException future-isDone : ${futureException.isDone} future-isCancelled : ${futureException.isCancelled}")
}
