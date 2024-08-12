/*
 * Copyright 2019 Sme.UP S.p.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smeup.rpgparser.parsing.parsetreetoast

import com.smeup.rpgparser.execution.MainExecutionContext
import com.smeup.rpgparser.execution.ParsingProgram
import com.smeup.rpgparser.interpreter.AbstractDataDefinition
import com.smeup.rpgparser.interpreter.DataDefinition
import com.smeup.rpgparser.interpreter.InStatementDataDefinition
import com.smeup.rpgparser.interpreter.type
import com.smeup.rpgparser.parsing.ast.*
import com.smeup.rpgparser.parsing.facade.AstCreatingException
import com.smeup.rpgparser.parsing.facade.getExecutionProgramNameWithNoExtension
import com.smeup.rpgparser.parsing.facade.getLastPoppedParsingProgram
import com.smeup.rpgparser.utils.popIfPresent
import com.smeup.rpgparser.utils.pushIfNotAlreadyPresent
import com.strumenta.kolasu.model.*
import com.strumenta.kolasu.validation.Error
import com.strumenta.kolasu.validation.ErrorType
import java.util.*

private fun List<CompositeStatement>.findWrappedInStatementDataDefinitions(): List<StatementThatCanDefineData> {
    if (this.isEmpty()) return emptyList()

    val candidates = this.flatMap { it.body }
    val free = candidates.filterIsInstance<StatementThatCanDefineData>()
    val unwrapped = candidates.filterIsInstance<CompositeStatement>().findWrappedInStatementDataDefinitions()
    return free + unwrapped
}

private fun List<StatementThatCanDefineData>.moveDefineStmtsToEnd(): List<StatementThatCanDefineData> {
    val defineStmts = this.filterIsInstance<DefineStmt>()
    val otherStmts = this.filter { it !is DefineStmt }
    return otherStmts + defineStmts
}

private fun CompilationUnit.findInStatementDataDefinitions() {
    // Filter related statements
    val candidates = this.allStatements(preserveCompositeStatement = true)
    val compositeStatements = candidates.filterIsInstance<CompositeStatement>()
    val freeStatements = candidates.filterIsInstance<StatementThatCanDefineData>()

    // Unwrap StatementThatCanDefineData contained in CompositeStatements
    val unwrappedCompositeStatements = compositeStatements.findWrappedInStatementDataDefinitions()

    // Move define statements to end as they can be based on other instatement definitions
    val targetStatements = (freeStatements + unwrappedCompositeStatements).moveDefineStmtsToEnd()

    targetStatements.forEach { statementThatCanDefineData ->
        kotlin.runCatching {
            this.addInStatementDataDefinitions(statementThatCanDefineData.dataDefinition())
        }.onFailure { error ->
            if (statementThatCanDefineData is Node) {
                kotlin.runCatching {
                    statementThatCanDefineData.error("Error while creating data definition from statement: $statementThatCanDefineData", error)
                }
            } else throw error
        }
    }
}

private fun MutableList<InStatementDataDefinition>.addAllDistinct(list: List<InStatementDataDefinition>): List<InStatementDataDefinition> {
    list.forEach { item ->
        run {
            if (this.isEmpty() || this.any { it.name != item.name }) {
                this.add(item)
            }
        }
    }
    return this
}

fun CompilationUnit.allStatements(preserveCompositeStatement: Boolean = false): List<Statement> {
    val result = mutableListOf<Statement>()
    result.addAll(this.main.stmts.explode(preserveCompositeStatement = preserveCompositeStatement))
    this.subroutines.forEach {
        result.addAll(it.stmts.explode(preserveCompositeStatement = preserveCompositeStatement))
    }
    return result
}

private fun Node.resolveDataRefs(cu: CompilationUnit) {
    runNode {
        this.specificProcess(DataRefExpr::class.java) { dre ->
            if (!dre.variable.resolved) {
                if (dre.variable.name.contains('.')) {
                    dre.variable.name.substring(0, dre.variable.name.indexOf("."))

                    val fieldName = dre.variable.name.substring(dre.variable.name.indexOf(".") + 1)

                    val resField = cu.allDataDefinitions.find { it.name.equals(fieldName, true) }
                    dre.variable.referred = resField
                } else {
                    var currentCu: CompilationUnit? = cu
                    var resolved = false
                    while (currentCu != null && !resolved) {
                        resolved = dre.variable.tryToResolve(currentCu.allDataDefinitions, caseInsensitive = true)
                        currentCu = currentCu.parent?.let { it as CompilationUnit }
                    }
                    if (!resolved) {
                        kotlin.runCatching { dre.error("Data reference not resolved: ${dre.variable.name}") }
                    }
                }
            }
        }
    }
}

private fun Node.resolveFunctionCalls(cu: CompilationUnit) {
    // replace FunctionCall with ArrayAccessExpr where it makes sense
    this.specificProcess(FunctionCall::class.java) { fc ->
        if (fc.args.size == 1) {
            val data = cu.allDataDefinitions.firstOrNull { it.name == fc.function.name }
            if (data != null) {
                fc.replace(ArrayAccessExpr(
                        array = DataRefExpr(ReferenceByName(fc.function.name, referred = data)),
                        index = fc.args[0],
                        position = fc.position))
            }
        }
    }
}

fun MuteAnnotation.resolveAndValidate(cu: CompilationUnit) {
    this.resolveDataRefs(cu)
    this.resolveFunctionCalls(cu)
}

/**
 * In case of semantic errors we could either raise exceptions or return a list of errors.
 *
 */
fun CompilationUnit.resolveAndValidate(): List<Error> {
    kotlin.runCatching {
        val parsingProgram = ParsingProgram(getExecutionProgramNameWithNoExtension())
        parsingProgram.copyBlocks = this.copyBlocks
        parsingProgram.sourceLines = getLastPoppedParsingProgram()?.sourceLines
        MainExecutionContext.getParsingProgramStack().pushIfNotAlreadyPresent(parsingProgram)
        this.resolve()
        checkAstCreationErrors(AstHandlingPhase.Resolution)
        return this.validate().apply {
            MainExecutionContext.getParsingProgramStack().popIfPresent()
        }
    }.onFailure {
        this.source?.let { source ->
            throw AstCreatingException(source, it)
        }
    }.getOrThrow()
}

class SemanticErrorsException(val errors: List<Error>) : RuntimeException("Semantic errors found: $errors")

/**
 * In case of semantic errors we could either raise exceptions or return a list of errors.
 */
private fun CompilationUnit.validate(): List<Error> {
    val errors = LinkedList<Error>()
    // TODO validate SubstExpr for assignability
    // TODO check initial value in DoStmt
    // No need to check Eval directly, we check the AssignmentExpr instead
    this.specificProcess(AssignmentExpr::class.java) {
        val targetType = it.target.type()
        val valueType = it.value.type()
        if (!targetType.canBeAssigned(valueType)) {
            errors.add(Error(ErrorType.SEMANTIC, "Invalid assignement: cannot assign ${it.value} having type $valueType to ${it.target} having type $targetType", it.position))
        }
    }
    if (errors.isNotEmpty()) {
        throw SemanticErrorsException(errors)
    }
    return errors
}

private fun CompilationUnit.resolve() {
    this.assignParents()

    this.findInStatementDataDefinitions()

    this.resolveDataRefs(this)

    this.specificProcess(ExecuteSubroutine::class.java) { esr ->
        if (!esr.subroutine.resolved) {
            kotlin.runCatching {
                esr.require(esr.subroutine.tryToResolve(this.subroutines, caseInsensitive = true)) {
                    "Subroutine call not resolved: ${esr.subroutine.name}"
                }
            }
        }
    }
    this.specificProcess(QualifiedAccessExpr::class.java) { qae ->
        if (!qae.field.resolved) {
            if (qae.container is DataRefExpr) {
                val dataRef = qae.container
                val dataDefinition = dataRef.variable.referred!! as DataDefinition
                qae.runNode {
                    kotlin.runCatching {
                        require(qae.field.tryToResolve(dataDefinition.fields, caseInsensitive = true)) {
                            "Field access not resolved: ${qae.field.name} in data definition ${dataDefinition.name}"
                        }
                    }
                }
            } else {
                qae.todo()
            }
        }
    }
    this.resolveFunctionCalls(this)

    // replace equality expr in For init with Assignments
    this.specificProcess(ForStmt::class.java) { fs ->
        if (fs.init is EqualityExpr) {
            val ee = fs.init as EqualityExpr
            fs.init.replace(AssignmentExpr(ee.left as AssignableExpression, ee.right, ee.position))
        }
    }

    this.specificProcess(EvalStmt::class.java) { s ->
        if (s.expression is EqualityExpr) {
            // See issue %57 during the code review
            // s.expression.replace((s.expression as EqualityExpr).toAssignment())
        }
    }

    this.specificProcess(PlistParam::class.java) { pp ->
        if (!pp.param.resolved) {
            pp.param.tryToResolveRecursively(position = pp.position, cu = this)
        }
    }

    this.allDataDefinitions
}

// try to resolve a Data reference through recursive search in parent compilation unit
private fun ReferenceByName<AbstractDataDefinition>.tryToResolveRecursively(position: Position? = null, cu: CompilationUnit) {
    var currentCu: CompilationUnit? = cu
    var resolved = false
    while (currentCu != null && !resolved) {
        resolved = this.tryToResolve(currentCu.allDataDefinitions, caseInsensitive = true)
        currentCu = currentCu.parent?.let { it as CompilationUnit }
    }
    require(resolved) {
        "Data reference not resolved: ${this.name} at $position"
    }
}
