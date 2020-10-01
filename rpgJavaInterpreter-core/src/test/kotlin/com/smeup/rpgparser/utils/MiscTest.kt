package com.smeup.rpgparser.utils

import org.apache.commons.io.FileUtils
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class MiscTest {

    @Test
    fun chunkLineTest() {
        assertEquals(listOf("a1", "b1", "c1", "a2", "b2", "c2"),
            listOf("a1b1c1d1", "a2b2c2xxxxxxx").chunkAs(3, 2))

        assertEquals(listOf("a1", "b1", "a2", "b2"),
            listOf("a1b1", "a2b2").chunkAs(3, 2))
    }

    @Test
    fun resizeToTest() {
        assertEquals(listOf("a", "b", "c"),
            listOf("a", "b", "c", "z", "z").resizeTo(3, "x"))

        assertEquals(listOf("a", "b", "x"),
            listOf("a", "b").resizeTo(3, "x"))
    }

    @Test
    fun divideAtIndexTest() {
        assertEquals(Pair("", "abcde"), "abcde".divideAtIndex(0))
        assertEquals(Pair("", "abcde"), "abcde".divideAtIndex(-1))
        assertEquals(Pair("abcde", ""), "abcde".divideAtIndex(20))
        assertEquals(Pair("abcde", ""), "abcde".divideAtIndex(4))
        assertEquals(Pair("a", "bcde"), "abcde".divideAtIndex(1))
        assertEquals(Pair("ab", "cde"), "abcde".divideAtIndex(2))
    }

    @Test
    fun repeatWithMaxSizeTest() {
        assertEquals("abcab", "abc".repeatWithMaxSize(5))
        assertEquals("aaaaa", "a".repeatWithMaxSize(5))
        assertEquals("ab", "abc".repeatWithMaxSize(2))
    }

    @Test
    fun runIfNotEmptyTest() {
        val nullSting: String? = null
        nullSting.runIfNotEmpty {
            fail("Should not be executed")
        }

        " ".runIfNotEmpty {
            fail("Should not be executed")
        }

        var result = "Lambda not invoked"
        "some string".runIfNotEmpty {
            result = "OK"
        }
        assertEquals("OK", result)
    }

    @Test
    fun compilerTest() {
        val dummyDir = "/impossibiledir12334567"
        assertEquals(
            true,
            compile(src = File(dummyDir), File(dummyDir).parentFile).isEmpty()
        )

        val program = "     ** String of 50 chars:\n" +
                "     D Msg             S             50\n" +
                "     ** Implicit declaration of a number with 3 digits, 2 of them are decimals\n" +
                "     C                   clear                   X                 3 2\n" +
                "     C                   eval      x = %ABS(-1)\n" +
                "     C                   eval      Msg = 'X is ' + %CHAR(X)\n" +
                "     C                   dsply                   Msg\n" +
                "     C                   SETON                                          LR"
        val srcFile = File.createTempFile("MYPGM", ".rpgle")
        srcFile.writeText(program)
        println("Compiling $srcFile")
        val compilationResults = compile(src = srcFile, srcFile.parentFile)
        println("Compilation results: $compilationResults")
        assertNotNull(compilationResults.first().compiledFile)
        assert(compilationResults.first().error == null)
        srcFile.delete()
        compilationResults.forEach {
            it.compiledFile?.delete()
        }
    }

    @Test
    fun compileTestBadFile() {
        val program = "BADRPG\n d a a a a a aa a a a a a a"
        val srcFile = File.createTempFile("MYPGM", ".rpgle")
        srcFile.deleteOnExit()
        srcFile.writeText(program)
        println("Compiling $srcFile")
        val compilationResults = compile(src = srcFile, srcFile.parentFile)
        assert(compilationResults.first().compiledFile == null)
        assertNotNull(compilationResults.first().parsingError)
    }

    @Test
    fun compileFromInToOut() {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val programName = "HELLO"
        val srcFile = File("src/test/resources/$programName.rpgle")
        val outBinFile = File("$tmpDir/$programName.bin")
        val outJsonFile = File("$tmpDir/$programName.json")

        outBinFile.deleteOnExit()
        outJsonFile.deleteOnExit()

        compile(srcFile.inputStream(), outBinFile.outputStream(), Format.BIN, false)
        val expectedBin = File("src/test/resources/$programName.bin")
        assertTrue(FileUtils.contentEquals(expectedBin, outBinFile))

        compile(srcFile.inputStream(), outJsonFile.outputStream(), Format.JSON, false)
        val expectedJson = File("src/test/resources/$programName.json")
        assertTrue(FileUtils.contentEquals(expectedJson, outJsonFile))
    }
}
