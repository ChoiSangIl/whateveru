package org.example

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.Future

class FutureTest {
    private fun helloFuture(): Future<String> {
        val executor = Executors.newSingleThreadExecutor()
        return try {
            executor.submit<String> {
                println("[${Thread.currentThread().name} executor.submit() 실행됨")
                "hello future!"
            }
        } finally {
            executor.shutdown()
        }
    }

    @Test
    @DisplayName("Future 테스트 - future.get() 함수는 Blocking 되고 isDone, isCancelled 상태를 갖는다.")
    fun getFutureTest() {
        val future: Future<String> = helloFuture()

        Assertions.assertFalse(future.isDone)
        Assertions.assertFalse(future.isCancelled)

        // future.get()은 블록킹 메소드
        val futureResult = future.get()

        Assertions.assertEquals("hello future!", futureResult)
        Assertions.assertTrue(future.isDone)
        Assertions.assertFalse(future.isCancelled)
    }

    private fun timeoutFuture(): Future<String> {
        val executor = Executors.newSingleThreadExecutor()
        return try {
            executor.submit<String> {
                println("[${Thread.currentThread().name} executor.submit() 실행됨")
                Thread.sleep(1000)
                "Hello, World!"
            }
        } finally {
            executor.shutdown()
        }
    }

    @Test
    @DisplayName("Future 테스트 - Timeout 설정 > timeout 시간동안 Thread 는 Blocking 된다")
    fun futureTimeoutTest() {
        val future: Future<String> = timeoutFuture()
        val futureResult = future.get(1500, java.util.concurrent.TimeUnit.MILLISECONDS)

        Assertions.assertEquals("Hello, World!", futureResult)
        Assertions.assertTrue(future.isDone)
        Assertions.assertFalse(future.isCancelled)
    }

    @Test
    @DisplayName("Future 테스트 - Timeout 설정 Exception > Timeout 시간보다 더 걸리면 TimeoutException 발생한다")
    fun futureTimeoutExceptionTest() {
        val future: Future<String> = timeoutFuture()

        Assertions.assertThrows(java.util.concurrent.TimeoutException::class.java) {
            future.get(500, java.util.concurrent.TimeUnit.MILLISECONDS)
        }
    }

    @Test
    @DisplayName("Future 테스트 - cancel > Future 를 취소할 수 있다, 취소된 future.get()은 CancellationException 발생한다")
    fun futureCancelTest() {
        val future: Future<String> = timeoutFuture()
        val cancel = future.cancel(true)

        Assertions.assertTrue(cancel)
        Assertions.assertTrue(future.isDone)
        Assertions.assertTrue(future.isCancelled)

        Assertions.assertThrows(java.util.concurrent.CancellationException::class.java) {
            future.get()
        }

        val cancelRepeat = future.cancel(true)
        Assertions.assertFalse(cancelRepeat)
    }

    private fun exceptionFuture(): Future<String> {
        val executor = Executors.newSingleThreadExecutor()
        return try {
            executor.submit<String> {
                throw IllegalArgumentException("Error")
            }
        } finally {
            executor.shutdown()
        }
    }

    @Test
    @DisplayName("Future 의 한계 > Exception 이 발생하든 cancel 이 되든 isDone 은 true 이다 > 완료되거나 에러가 발생했는지 구분이 어렵다 > 오류 헨들링이 어렵다")
    fun future(){
        val future = helloFuture()
        future.cancel(true)
        Assertions.assertTrue(future.isDone)

        val exceptionFuture = exceptionFuture()
        Assertions.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            exceptionFuture.get()
        }
        Assertions.assertTrue(exceptionFuture.isDone)
    }
}
