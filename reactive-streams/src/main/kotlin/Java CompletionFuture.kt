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
    @DisplayName(
        """
            thenAccept 는 FUTURE STAGE 가 DONE 상태이면 메인 THREAD 에서 실행 됨. finishedStage 블락되는 작업이 함께 있으면 메인 스레드가 블락될 수 있음. 
            비동기 작업이 빨리 끝났는데 Block 되는 작업이 있다면 main thread 가 블락킹 될 수 있음 > Blocking 비동기 상태가 된다
        """
    )
    fun thenAcceptFinishedStageTest(){
        printlnWithThreadName("start main")

        finishedStage()
            .thenAccept{ printlnWithThreadName("thenAccept >> $it") }
            .thenAccept{ printlnWithThreadName("thenAccept2 >> $it") }

        printlnWithThreadName("after thenAccept")

        Thread.sleep(100)
    }


    @Test
    @DisplayName("thenAcceptAsync 는 FUTURE STAGE 가 DONE 상태여도 별도의 thread pool 에서 실행됨")
    fun thenAsyncAcceptFinishedStageTest(){
        println("[${Thread.currentThread().name}] start main")

        finishedStage()
            .thenAcceptAsync{ printlnWithThreadName("thenAcceptAsync >> $it") }
            .thenAcceptAsync{ printlnWithThreadName("thenAcceptAsync2 >> $it") }

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

    @Test
    @DisplayName("thenAccept 는 FUTURE STAGE 가 RUNNING 상태이면 별도의 Thread 에서 실행 됨")
    fun thenAcceptRunningStageTest(){
        printlnWithThreadName("start main")

        runningStage()
            .thenAccept{ printlnWithThreadName("thenAccept >> $it") }
            .thenAccept{ printlnWithThreadName("thenAccept2 >> $it") }

        printlnWithThreadName("after thenAccept")

        Thread.sleep(2000)
    }

    @Test
    @DisplayName("thenAcceptAsync 는 FUTURE STAGE 가 RUNNING 상태이면 별도의 Thread 에서 실행 됨")
    fun thenAcceptAsyncRunningStageTest(){
        printlnWithThreadName("start main")

        runningStage()
            .thenAcceptAsync{ printlnWithThreadName("thenAcceptAsync >> $it") }
            .thenAcceptAsync{ printlnWithThreadName("thenAcceptAsync2 >> $it") }

        printlnWithThreadName("after thenAccept")

        Thread.sleep(2000)
    }
}