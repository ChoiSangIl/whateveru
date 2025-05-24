package study.javaio

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import study.util.printlnWithThreadName
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class JavaNioStudy{

    @Test
    @DisplayName("java io ByteArrayInputStream 테스트")
    fun byteArrayInputStream(){
        val bytes = byteArrayOf(100, 101, 102, 103, 104)

        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var value: Int

        while (byteArrayInputStream.read().also { value = it } != -1) {
            printlnWithThreadName("value: $value")
        }
    }

    @Test
    @DisplayName("java io FileInputStream")
    fun fileInputStream(){
        println("start main")

        try {
            val file = File(
                object {}.javaClass.classLoader
                    .getResource("data.txt")!!
                    .file
            )

            FileInputStream(file).use { fis ->
                var value: Int

                while (fis.read().also { value = it } != -1) {
                    printlnWithThreadName(value.toChar())  // 문자 출력
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        println("\nend main")

    }

}