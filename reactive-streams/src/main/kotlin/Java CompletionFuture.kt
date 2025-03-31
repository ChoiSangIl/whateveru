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
    private fun finishedStage(): CompletionStage<String> {
        val future =  CompletableFuture.supplyAsync {
            printlnWithThreadName("return helloFinishedStage")
            "Hello, CompletableFuture!"
        }

        Thread.sleep(100)
        return future
    }

    @Test
    @DisplayName("thenAccept 는 FUTURE STAGE 가 DONE 상태이면 메인 THREAD 에서 실행 됨. finishedStage 블락되는 작업이 함께 있으면 메인 스레드가 블락될 수 있음.")
    fun thenAcceptFinishedStageTest(){
        printlnWithThreadName("start main")

        finishedStage()
            .thenAccept(::printlnWithThreadName)
            .thenAccept(::printlnWithThreadName)

        printlnWithThreadName("after thenAccept")

        Thread.sleep(100)
    }


    @Test
    @DisplayName("thenAcceptAsync 는 FUTURE STAGE 가 DONE 상태여도 별도의 thread pool 에서 실행됨")
    fun thenAsyncAcceptFinishedStageTest(){
        println("[${Thread.currentThread().name}] start main")

        finishedStage()
            .thenAcceptAsync(::printlnWithThreadName)
            .thenAcceptAsync(::printlnWithThreadName)

        printlnWithThreadName("after thenAccept")

        Thread.sleep(100)
    }

    private fun runningStage(): CompletionStage<String> {
        return CompletableFuture.supplyAsync {
            Thread.sleep(1000)
            printlnWithThreadName("return helloRunningStage")
            "Hello, CompletableFuture!"
        }
    }

}