package study.util

import java.time.LocalDateTime

fun printlnWithThreadName(message: Any?) {
    println("${LocalDateTime.now()} [${Thread.currentThread().name}] $message")
}