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

    @Test
    @DisplayName("completableFuture 는 완료 상태로 변경할 수 있고 이미 완료 된 경우 FALSE 를 반환 한다.")
    fun completeTest() {
        val future = CompletableFuture<String>()

        assert(!future.isDone)

        var triggered = future.complete("completed")
        assert(future.isDone)
        assert(triggered)
        assert(future.get() == "completed")

        triggered = future.complete("completed2")
        assert(future.isDone)
        assert(!triggered)
        assert(future.get() == "completed")
    }

    @Test
    @DisplayName("completableFuture 는 isCompletedExceptionally 로 오류가 났는지 확인할 수 있다.")
    fun isCompletedExceptionallyTest(){
        val futureWithException = CompletableFuture.supplyAsync{
            return@supplyAsync 1/0
        }
        Thread.sleep(100)

        assert(futureWithException.isDone)
        assert(futureWithException.isCompletedExceptionally)
    }

    fun waitAndReturn(millis: Int, value: Int): CompletableFuture<Int> {
        return CompletableFuture.supplyAsync<Int> {
            try {
                printlnWithThreadName("waitAndReturn: {$millis}ms")
                Thread.sleep(millis.toLong())
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            value
        }
    }

    @Test
    @DisplayName("CompletableFuture.allOf() 를 사용하면 각 Future 가 완료될 때까지 기다린다.")
    fun allOfTest(){
        printlnWithThreadName("start")

        val firstFuture = waitAndReturn(100, 1)
        val secondFuture = waitAndReturn(500, 2)
        val thirdFuture = waitAndReturn(3000, 3)

        CompletableFuture.allOf(firstFuture, secondFuture, thirdFuture).thenAcceptAsync{
            printlnWithThreadName("after allOf")
            printlnWithThreadName(firstFuture.get())
            printlnWithThreadName(secondFuture.get())
            printlnWithThreadName(thirdFuture.get())
        }.join()

        println("end")
    }

    @Test
    @DisplayName("anyOf 의 경우 가장 빨리 끝난 Future 의 결과를 가져온다.")
    fun anyOfTest(){
        printlnWithThreadName("start")

        val firstFuture = waitAndReturn(500, 1)
        val secondFuture = waitAndReturn(100, 2)
        val thirdFuture = waitAndReturn(3000, 3)

        CompletableFuture.anyOf(firstFuture, secondFuture, thirdFuture).thenAcceptAsync{
            printlnWithThreadName("after anyOf")
            printlnWithThreadName("first value $it")
        }.join()

        println("end")
    }
}