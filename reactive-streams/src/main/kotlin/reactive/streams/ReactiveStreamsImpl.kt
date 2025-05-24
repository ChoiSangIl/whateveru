package reactive.streams

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.Executors

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class SimplePublisher(private val data: List<Int>) : Publisher<Int> {
    override fun subscribe(subscriber: Subscriber<in Int>) {
        val subscription = AsyncSubscription(subscriber, data)
        subscriber.onSubscribe(subscription)
    }

    private class AsyncSubscription(
        private val subscriber: Subscriber<in Int>,
        private val data: List<Int>
    ) : Subscription {
        private val requested = AtomicLong(0)
        private val cancelled = AtomicBoolean(false)
        private val emitting = AtomicBoolean(false)
        private var index = 0
        private val executor = Executors.newSingleThreadExecutor()

        override fun request(n: Long) {
            if (n <= 0) {
                subscriber.onError(IllegalArgumentException("Request must be > 0"))
                return
            }

            requested.addAndGet(n)
            tryEmit()
        }

        override fun cancel() {
            cancelled.set(true)
            executor.shutdownNow()
        }

        private fun tryEmit() {
            if (emitting.compareAndSet(false, true)) {
                executor.submit {
                    try {
                        while (requested.get() > 0 && index < data.size && !cancelled.get()) {
                            Thread.sleep(300)
                            subscriber.onNext(data[index++])
                            requested.decrementAndGet()
                        }

                        if (index == data.size && !cancelled.get()) {
                            subscriber.onComplete()
                            executor.shutdown()
                        }
                    } catch (e: Exception) {
                        subscriber.onError(e)
                        executor.shutdown()
                    } finally {
                        emitting.set(false)
                        // 남은 요청이 있으면 다시 emit
                        if (requested.get() > 0 && index < data.size && !cancelled.get()) {
                            tryEmit()
                        }
                    }
                }
            }
        }
    }
}


class SimpleSubscriber : Subscriber<Int> {
    private var subscription: Subscription? = null

    override fun onSubscribe(s: Subscription) {
        println("[${Thread.currentThread().name}] Subscribed")
        subscription = s
        s.request(1)
    }

    override fun onNext(t: Int) {
        println("[${Thread.currentThread().name}] Received: $t")
        Thread.sleep(500)
        subscription?.request(1)
    }

    override fun onError(t: Throwable) {
        println("[${Thread.currentThread().name}] Error: ${t.message}")
    }

    override fun onComplete() {
        println("[${Thread.currentThread().name}] Done!")
    }
}

fun main() {
    println("[${Thread.currentThread().name}] main thread start...")
    val publisher = SimplePublisher((1..10).toList())
    val subscriber = SimpleSubscriber()
    publisher.subscribe(subscriber = subscriber)
}