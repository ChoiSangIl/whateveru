package org.example

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

// Custom Publisher Implementation
class SimplePublisher(private val data: List<Int>) : Publisher<Int> {
    override fun subscribe(subscriber: Subscriber<in Int>) {
        val subscription = SimpleSubscription(subscriber, data)
        subscriber.onSubscribe(subscription)
    }
}

class SimpleSubscription(
    private val subscriber: Subscriber<in Int>,
    private val data: List<Int>
) : Subscription {

    private var canceled = false
    private var currentIndex = 0
    private var isCompleted = false // 상태 플래그 추가

    override fun request(n: Long) {
        println(n)
        if (canceled || isCompleted) return

        var sent = 0L
        while (sent < n && currentIndex < data.size) {
            subscriber.onNext(data[currentIndex++])
            sent++
        }

        // 모든 데이터가 전달되었고, 완료 상태가 아니라면 onComplete 호출
        if (currentIndex == data.size && !isCompleted) {
            isCompleted = true
            subscriber.onComplete()
        }
    }

    override fun cancel() {
        canceled = true
    }
}

// Custom Subscriber Implementation
class SimpleSubscriber : Subscriber<Int> {
    private var subscription: Subscription? = null

    override fun onSubscribe(subscription: Subscription) {
        println("Subscribed!")
        this.subscription = subscription
        subscription.request(3) // Request 3 items initially
    }

    override fun onNext(item: Int) {
        println("Received: $item")
        subscription?.request(1) // Request one more item on each `onNext`
    }

    override fun onError(throwable: Throwable) {
        println("Error: ${throwable.message}")
    }

    override fun onComplete() {
        println("Completed!")
    }
}

// Main function to test
fun main() {
    val publisher = SimplePublisher(listOf(1, 2, 3, 4, 5,6,7,8,9))
    val subscriber1 = SimpleSubscriber()
    val subscriber2 = SimpleSubscriber()

    publisher.subscribe(subscriber1)
    publisher.subscribe(subscriber2)
}
