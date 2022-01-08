package me.wsj.fengyun

import com.google.gson.Gson
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
        println(result.toString())
        val str = "['a','b','c']"
        val data = Gson().fromJson<List<String>>(str)
        println(data)
    }

    inline fun <reified T : Any> Gson.fromJson(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }
}