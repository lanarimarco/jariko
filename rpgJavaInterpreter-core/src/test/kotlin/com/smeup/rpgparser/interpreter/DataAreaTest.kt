package com.smeup.rpgparser.interpreter

import com.smeup.rpgparser.AbstractTest
import com.smeup.rpgparser.execution.Configuration
import com.smeup.rpgparser.execution.JarikoCallback
import com.smeup.rpgparser.jvminterop.JavaSystemInterface
import org.junit.Test
import kotlin.test.assertTrue

class DataAreaTest : AbstractTest() {

    @Test
    fun testInStmtCallsReadDataAreaCallback() {
        val readDataAreaCalls = mutableListOf<Triple<String, Boolean, String>>() // dataAreaName, lock, returnValue
        val writeDataAreaCalls = mutableListOf<Triple<String, String, Boolean>>() // dataAreaName, value, lock

        val configuration = Configuration().apply {
            jarikoCallback = JarikoCallback().apply {
                readDataArea = { dataAreaName, lock ->
                    val returnValue = "DATA_FROM_$dataAreaName"
                    readDataAreaCalls.add(Triple(dataAreaName, lock, returnValue))
                    returnValue
                }
                writeDataArea = { dataAreaName, value, lock ->
                    writeDataAreaCalls.add(Triple(dataAreaName, value, lock))
                }
            }
        }

        val systemInterface = JavaSystemInterface().apply {
            onDisplay = { message, _ ->
                println("DSPLY: $message")
            }
        }

        try {
            executePgm("DTAREAREAD", configuration = configuration, systemInterface = systemInterface)

            // Verify that readDataArea callback was called
            assertTrue(readDataAreaCalls.isNotEmpty(), "readDataArea callback should have been called")

            // Check the first call
            val firstCall = readDataAreaCalls.first()
            assertTrue(firstCall.second, "Lock should be true") // *LOCK was specified

            println("ReadDataArea calls: $readDataAreaCalls")
            println("WriteDataArea calls: $writeDataAreaCalls")
        } catch (e: Exception) {
            println("Error executing test program: ${e.message}")
            e.printStackTrace()
        }
    }

    @Test
    fun testOutStmtCallsWriteDataAreaCallback() {
        val writeDataAreaCalls = mutableListOf<Triple<String, String, Boolean>>()

        val testProgram = """
     D SCAATTDS        DS           460
     C     *DTAARA       DEFINE    C£C£E00D      SCAATTDS
     C                   EVAL      SCAATTDS='WRITTEN'
     C                   OUT       SCAATTDS
     C     SCAATTDS      DSPLY
        """.trimIndent()

        val configuration = Configuration().apply {
            jarikoCallback = JarikoCallback().apply {
                writeDataArea = { dataAreaName, value, lock ->
                    writeDataAreaCalls.add(Triple(dataAreaName, value, lock))
                }
            }
        }

        val systemInterface = JavaSystemInterface().apply {
            onDisplay = { message, _ ->
                println("DSPLY: $message")
            }
        }

        try {
            executePgm(programName = testProgram, systemInterface = systemInterface, configuration = configuration)

            // Verify that writeDataArea callback was called
            assertTrue(writeDataAreaCalls.isNotEmpty(), "writeDataArea callback should have been called")

            println("WriteDataArea calls: $writeDataAreaCalls")
        } catch (e: Exception) {
            println("Error executing test program: ${e.message}")
            e.printStackTrace()
        }
    }
}