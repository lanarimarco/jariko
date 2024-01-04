package com.smeup.rpgparser.evaluation

import com.smeup.rpgparser.AbstractTest
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class SmeupInterpreterTest : AbstractTest() {

    @Test
    fun executeT15_A80() {
        // TODO When we will have more clear idea about the expected result, we will add the assert
        println("executeT15_A80: " + "smeup/T15_A80".outputOf())
    }

    @Test
    fun executeT15_A90() {
        // TODO When we will have more clear idea about the expected result, we will add the assert
        println("executeT15_A90: " + "smeup/T15_A90".outputOf())
    }

    @Test
    fun executeT02_A20() {
        val values = "smeup/T02_A20".outputOf()
        assertTrue(values[0].matches(Regex("A20_Z1\\(\\d{4}-\\d{2}-\\d{2}-\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{6}\\)")))
        assertEquals("A20_Z2(2003-06-27-09.25.59.123456)", values[1])
    }

    @Test
    fun executeT02_A30() {
        val len = 100
        val expected = listOf(
            buildString {
                append("AAAAA".padEnd(len, ' '))
                append("BBBBB".padEnd(len, ' '))
                append("CCCCC".padEnd(len, ' '))
                append("DDDDD".padEnd(len, ' '))
                append("EEEEE".padEnd(len, ' '))
                append("FFFFF".padEnd(len, ' '))
                append("GGGGG".padEnd(len, ' '))
                append("HHHHH".padEnd(len, ' '))
                // Here I don't padEnd because the display messages are trimmed
                append("IIIII")
            }
        )
        assertEquals(expected, "smeup/T02_A30".outputOf())
    }

    @Test
    fun executeT02_A40() {
        val expected = listOf("DS4_FL1(NNNNNFFFFFMMMMMGGGGGAAAAAZZZZZ)", "DS4_FL2(AAAAAZZZZZMMMMMGGGGGNNNNNFFFFF)")
        assertEquals(expected, "smeup/T02_A40".outputOf())
    }

    @Test
    fun executeT04_A20() {
        val expected = listOf("CALL_1(MULANGT04 , 1, MULANGTB10: MULANGT04 chiamata 1                  ) CALL_2(MULANGT04 , 3, MULANGTB10: MULANGT04 chiamata 1                  )")
        assertEquals(expected, "smeup/T04_A20".outputOf())
    }

    @Test
    fun executeT04_A40() {
        val expected = listOf("A40_P1(122469.88)A40_P2(987.22)A40_P3(123456.10)A40_P4(121028170.03)")
        assertEquals(expected, "smeup/T04_A40".outputOf())
    }

    @Test
    fun executeT04_A80() {
        val actual = "smeup/T04_A80".outputOf()
        val t = LocalDateTime.now()
        val expected = listOf(
            DateTimeFormatter.ofPattern("Hmmss").format(t),
            DateTimeFormatter.ofPattern("HmmssddMMyy").format(t),
            DateTimeFormatter.ofPattern("HmmssddMMyyyy").format(t)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun executeT10_A20() {
        val expected = listOf("172.670-22146.863-.987000000")
        assertEquals(expected, "smeup/T10_A20".outputOf())
    }

    @Test
    fun executeT10_A70() {
        val expected = listOf("CAT_1(MR. SMITH) CAT_2(RPG/400) CAT_3(RPG/4)", "CAT_1(ABC  XYZ ) CAT_2(Mr. Smith)", "CAT_1(RPG/400   )", "CAT_1(RPGIV     )")
        assertEquals(expected, "smeup/T10_A70".outputOf())
    }

    @Test
    fun executeT16_A20() {
        val expected = listOf("(Ontario, Canada)", "(Ontario, California     )", "(Ontario, Ontario, California     )", "(Somewhere else: Ontario, Ontario, California     )")
        assertEquals(expected, "smeup/T16_A20".outputOf())
    }

    @Test
    fun executeT16_A70() {
        val expected = listOf("A70_AR1(10) A70_AR2(20) A70_DS1(30) A70_AR3(10)")
        assertEquals(expected, "smeup/T16_A70".outputOf())
    }

    @Test
    fun executeT10_A90() {
        val expected = listOf("999-9999", "A90_A4(        ) A90_A5(RPG DEPT)", "A90_A4(        ) A90_A5(RPG DEPT)")
        assertEquals(expected, "smeup/T10_A90".outputOf())
    }
}