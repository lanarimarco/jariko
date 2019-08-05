package com.smeup.rpgparser.parsing

import com.smeup.rpgparser.ast.CompilationUnit
import com.smeup.rpgparser.facade.RpgParserFacade
import com.smeup.rpgparser.facade.RpgParserResult
import com.smeup.rpgparser.inputStreamFor
import com.smeup.rpgparser.parsetreetoast.ToAstConfiguration
import com.smeup.rpgparser.parsetreetoast.injectMuteAnnotation
import com.smeup.rpgparser.parsetreetoast.toAst
import java.lang.IllegalStateException
import kotlin.test.assertTrue
import org.junit.Test

public class RpgParserWithMuteSyntaxtTest {

    // Temporary replacement
    private fun assertASTCanBeProduced(
        exampleName: String,
        considerPosition: Boolean = false,
        withMuteSupport: Boolean = false
    ): CompilationUnit {
        val parseTreeRoot = assertCanBeParsed(exampleName, withMuteSupport)
        val ast = parseTreeRoot.root!!.rContext.toAst(ToAstConfiguration(
                considerPosition = considerPosition))
        if (withMuteSupport) {
            if (!considerPosition) {
                throw IllegalStateException("Mute annotations can be injected only when retaining the position")
            }
        }
        if (withMuteSupport) {
            ast.injectMuteAnnotation(parseTreeRoot.root!!.muteContexts!!)
        }
        return ast
    }
    // Temporary replacement to return RpgParserResult
    private fun assertCanBeParsed(exampleName: String, withMuteSupport: Boolean = false): RpgParserResult {
        val result = RpgParserFacade()
                .apply { this.muteSupport = withMuteSupport }
                .parse(inputStreamFor(exampleName))
        assertTrue(result.correct,
                message = "Errors: ${result.errors.joinToString(separator = ", ")}")

        return result
    }

    @Test
    fun parseMUTE01_syntax() {
        val result = assertCanBeParsed("mute/MUTE01_SYNTAX", withMuteSupport = true)
    }

    @Test
    fun parseMUTE01_ast() {
        val cu = assertASTCanBeProduced("mute/MUTE01_SYNTAX", considerPosition = true, withMuteSupport = true)
    }
}