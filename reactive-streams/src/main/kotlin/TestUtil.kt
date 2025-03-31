package org.example

fun printlnWithThreadName(message: Any?) {
    println("[${Thread.currentThread().name}] $message")
}