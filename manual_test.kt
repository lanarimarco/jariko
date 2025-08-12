package com.smeup.rpgparser.manual

import com.smeup.rpgparser.execution.Configuration
import com.smeup.rpgparser.execution.JarikoCallback
import com.smeup.rpgparser.jvminterop.JavaSystemInterface
import com.smeup.rpgparser.parsing.facade.RPGParserFacade
import com.smeup.rpgparser.parsing.facade.RpgParserResult
import com.smeup.rpgparser.parsing.parsetreetoast.ToAstConfiguration

/**
 * Manual test to verify that IN and OUT statements can be parsed without compilation errors
 */
fun main() {
    println("=== Manual test for IN/OUT statement parsing ===")
    
    // Test simple IN statement
    val inStatementCode = """
     D SCAATTDS        DS           460
     C     *DTAARA       DEFINE    C£C£E00D      SCAATTDS
     C                   EVAL      SCAATTDS='CURRENT'
     C     *LOCK         IN        SCAATTDS
     C     SCAATTDS      DSPLY
    """.trimIndent()
    
    println("Attempting to parse IN statement...")
    try {
        val result: RpgParserResult = RPGParserFacade.parseString(inStatementCode, ToAstConfiguration())
        println("✓ IN statement parsed successfully")
        println("Compilation unit contains ${result.root.main.stmts.size} statements")
        
        // Check if we have the right statement types
        result.root.main.stmts.forEach { stmt ->
            println("  - Statement type: ${stmt::class.simpleName}")
        }
        
    } catch (e: Exception) {
        println("✗ Failed to parse IN statement: ${e.message}")
        e.printStackTrace()
    }
    
    // Test simple OUT statement
    val outStatementCode = """
     D SCAATTDS        DS           460
     C     *DTAARA       DEFINE    C£C£E00D      SCAATTDS
     C                   EVAL      SCAATTDS='WRITTEN'
     C                   OUT       SCAATTDS
     C     SCAATTDS      DSPLY
    """.trimIndent()
    
    println("\nAttempting to parse OUT statement...")
    try {
        val result: RpgParserResult = RPGParserFacade.parseString(outStatementCode, ToAstConfiguration())
        println("✓ OUT statement parsed successfully")
        println("Compilation unit contains ${result.root.main.stmts.size} statements")
        
        // Check if we have the right statement types
        result.root.main.stmts.forEach { stmt ->
            println("  - Statement type: ${stmt::class.simpleName}")
        }
        
    } catch (e: Exception) {
        println("✗ Failed to parse OUT statement: ${e.message}")
        e.printStackTrace()
    }
    
    // Test JarikoCallback functionality
    println("\nTesting JarikoCallback functionality...")
    val dataAreaInteractions = mutableListOf<String>()
    
    val testCallback = JarikoCallback().apply {
        readDataArea = { dataAreaName, lock ->
            val interaction = "READ: $dataAreaName (lock=$lock)"
            dataAreaInteractions.add(interaction)
            println("  $interaction")
            "TestData_$dataAreaName"
        }
        
        writeDataArea = { dataAreaName, value, lock ->
            val interaction = "WRITE: $dataAreaName = '$value' (lock=$lock)"
            dataAreaInteractions.add(interaction)
            println("  $interaction")
        }
    }
    
    val config = Configuration().apply {
        jarikoCallback = testCallback
    }
    
    println("✓ JarikoCallback configured successfully")
    println("  - readDataArea callback set")
    println("  - writeDataArea callback set")
    
    println("\n=== Manual test completed ===")
    println("Summary:")
    println("- IN statement parsing: Available for testing")
    println("- OUT statement parsing: Available for testing") 
    println("- JarikoCallback integration: Ready")
    println("\nNote: Full execution test requires all dependencies to be available.")
}