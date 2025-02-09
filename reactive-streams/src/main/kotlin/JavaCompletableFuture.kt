package org.example


import java.util.concurrent.Executors
import java.util.concurrent.Future

fun getFuture(): Future<String> {
    val executor = Executors.newSingleThreadExecutor()
    return try {
         executor.submit<String> {
             println("getFuture 실행됨")
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

fun main(){
    val future: Future<String> = getFuture()
    // future 의 상태 조회
    println("future-isDone : ${future.isDone} future-isCancelled : ${future.isCancelled}")
}
