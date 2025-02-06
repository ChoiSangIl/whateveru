package org.example

import java.util.concurrent.ExecutorService
import java.util.concurrent.Flow
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Future


internal class OneShotPublisher : Flow.Publisher<Boolean?> {
    private val executor: ExecutorService = ForkJoinPool.commonPool() // daemon-based
    private var subscribed = false // true after first subscribe

    @Synchronized
    override fun subscribe(subscriber: Flow.Subscriber<in Boolean?>) {
        if (subscribed) subscriber.onError(IllegalStateException()) // only one allowed
        else {
            subscribed = true
            subscriber.onSubscribe(OneShotSubscription(subscriber, executor))
        }
    }

    internal class OneShotSubscription(
        subscriber: Flow.Subscriber<in Boolean?>,
        private val executor: ExecutorService
    ) : Flow.Subscription {
        private val subscriber: Flow.Subscriber<in Boolean> = subscriber
        private var future: Future<*>? = null // to allow cancellation
        private var completed = false

        @Synchronized
        override fun request(n: Long) {
            if (n != 0L && !completed) {
                completed = true
                if (n < 0) {
                    val ex = IllegalArgumentException()
                    executor.execute { subscriber.onError(ex) }
                } else {
                    future = executor.submit {
                        subscriber.onNext(java.lang.Boolean.TRUE)
                        subscriber.onComplete()
                    }
                }
            }
        }

        @Synchronized
        override fun cancel() {
            completed = true
            if (future != null) future!!.cancel(false)
        }
    }
}