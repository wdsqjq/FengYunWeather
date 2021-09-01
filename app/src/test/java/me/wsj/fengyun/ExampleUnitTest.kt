package me.wsj.fengyun

import org.junit.Test

import org.junit.Assert.*
import java.lang.Math.PI
import java.lang.Math.sin

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val result = sin(90 / 180F * PI)
        print(result.toString())


    }
}