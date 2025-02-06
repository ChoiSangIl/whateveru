package org.example


import java.util.concurrent.Executors
import java.util.concurrent.Future

fun getFuture(): Future<String> {
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
    val helloWorld: String = getFuture().get()
    println(helloWorld)
}
