package org.example

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/**
 * CompletionStage Interface
 * @see CompletionStage
 */
class CompletionFutureStudy{
    private fun helloFinishedStage(): CompletionStage<String> {
        val future =  CompletableFuture.supplyAsync {
            printlnWithThreadName("helloFinishedStage start...")
            "Hello, CompletableFuture!"
        }

        Thread.sleep(100)
        return future
    }

    private fun helloRunningStage(): CompletionStage<String> {
        val future = CompletableFuture.supplyAsync {
            Thread.sleep(100)
            printlnWithThreadName("helloRunningStage start...!")
            "Hello, CompletableFuture!"
        }
        return future
    }

    @Test
    @DisplayName("CompletionStage 테스트 - thenAccept()")
    fun thenAcceptTest(){
        printlnWithThreadName("start")

        helloFinishedStage()
            .thenAccept(::printlnWithThreadName)
            .thenAccept(::printlnWithThreadName)

        printlnWithThreadName("after")

        Thread.sleep(2000)
    }


    @Test
    @DisplayName("CompletionStage 테스트 - thenAccept()")
    fun thenAcceptTest2(){
        println("[${Thread.currentThread().name}] start")

        helloRunningStage()
            .thenAccept(::printlnWithThreadName)
            .thenAccept(::printlnWithThreadName)

        println("[${Thread.currentThread().name}] after")

        Thread.sleep(2000)
    }
}