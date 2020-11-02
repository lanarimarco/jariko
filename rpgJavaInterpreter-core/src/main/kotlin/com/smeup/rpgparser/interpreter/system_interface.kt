package com.smeup.rpgparser.interpreter

import com.smeup.dbnative.file.DBFile
import com.smeup.dbnative.file.RecordField
import com.smeup.dbnative.model.FileMetadata

import com.smeup.rpgparser.logging.configureLog
import com.smeup.rpgparser.logging.defaultLoggingConfiguration
import com.smeup.rpgparser.logging.loadLogConfiguration
import com.smeup.rpgparser.parsing.ast.MuteAnnotationExecuted
import com.smeup.rpgparser.rpginterop.RpgProgramFinder
import java.io.File
import java.io.PrintStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

typealias LoggingConfiguration = Properties

fun consoleVerboseConfiguration(): LoggingConfiguration {
    val props = Properties()
    props.setProperty("logger.data.separator", "\t")
    props.setProperty("logger.date.pattern", "HH:mm:ss.SSS")
    props.setProperty("data.level", "all")
    props.setProperty("data.output", "console")
    props.setProperty("loop.level", "all")
    props.setProperty("loop.output", "console")

    props.setProperty("expression.level", "all")
    props.setProperty("expression.output", "console")

    props.setProperty("statement.level", "all")
    props.setProperty("statement.output", "console")

    props.setProperty("performance.level", "all")
    props.setProperty("performance.output", "console")

    props.setProperty("resolution.level", "all")
    props.setProperty("resolution.output", "console")
    return LoggingConfiguration(props)
}

/**
 * This represent the interface to the external world.
 * Printing, accessing databases, all sort of interactions should go through this interface.
 */
interface SystemInterface {
    fun display(value: String)
    fun findProgram(name: String): Program?
    fun findFunction(globalSymbolTable: ISymbolTable, name: String): Function?

    val db: DBFile

    fun loggingConfiguration(): LoggingConfiguration?
    fun addExtraLogHandlers(logHandlers: List<InterpreterLogHandler>): SystemInterface {
        extraLogHandlers.addAll(logHandlers)
        return this
    }

    val extraLogHandlers: MutableList<InterpreterLogHandler>
    fun getAllLogHandlers() = (configureLog(
        this.loggingConfiguration() ?: defaultLoggingConfiguration()
    ) + this.extraLogHandlers).toMutableList()

    val executedAnnotationInternal: LinkedHashMap<Int, MuteAnnotationExecuted>
    fun getExecutedAnnotation(): LinkedHashMap<Int, MuteAnnotationExecuted>
    fun addExecutedAnnotation(line: Int, annotation: MuteAnnotationExecuted)
    fun registerProgramExecutionStart(program: Program, params: Map<String, Value>) {
        // do nothing by default
    }

    fun getFeaturesFactory() = FeaturesFactory.newInstance()
}

object DummySystemInterface : SystemInterface {
    override var executedAnnotationInternal: LinkedHashMap<Int, MuteAnnotationExecuted> =
        LinkedHashMap<Int, MuteAnnotationExecuted>()
    override var extraLogHandlers: MutableList<InterpreterLogHandler> = mutableListOf()

    override fun loggingConfiguration(): LoggingConfiguration? = null

    override val db: DBFile = TODO()

    override fun findFunction(globalSymbolTable: ISymbolTable, name: String): Function? {
        return null
    }

    override fun findProgram(name: String): Program? {
        return null
    }

    override fun display(value: String) {
        // doing nothing
    }

    override fun addExecutedAnnotation(line: Int, annotation: MuteAnnotationExecuted) {
        executedAnnotationInternal[line] = annotation
    }

    override fun getExecutedAnnotation(): LinkedHashMap<Int, MuteAnnotationExecuted> {
        return executedAnnotationInternal
    }
}

class SimpleSystemInterface(
    var loggingConfiguration: LoggingConfiguration? = null,
    val programFinders: List<RpgProgramFinder> = emptyList(),
    val output: PrintStream? = null
) : SystemInterface {
    override var executedAnnotationInternal: LinkedHashMap<Int, MuteAnnotationExecuted> =
        LinkedHashMap<Int, MuteAnnotationExecuted>()
    override var extraLogHandlers: MutableList<InterpreterLogHandler> = mutableListOf()

    override fun loggingConfiguration(): LoggingConfiguration? = this.loggingConfiguration

    override val db: DBFile = TODO()

    override fun findFunction(globalSymbolTable: ISymbolTable, name: String): Function? {
        return null
    }

    private val programs = java.util.HashMap<String, Program?>()

    override fun findProgram(name: String): Program? {
        programs.computeIfAbsent(name) {
            programFinders.asSequence().mapNotNull {
                it.findRpgProgram(name, db)
            }.firstOrNull()
        }
        return programs[name]
    }

    override fun display(value: String) {
        output?.println(value)
    }

    fun useConfigurationFile(configurationFile: File?): SystemInterface {
        if (configurationFile == null) {
            this.loggingConfiguration = defaultLoggingConfiguration()
        } else {
            this.loggingConfiguration = loadLogConfiguration(configurationFile)
        }
        return this
    }

    override fun addExecutedAnnotation(line: Int, annotation: MuteAnnotationExecuted) {
        executedAnnotationInternal[line] = annotation
    }

    override fun getExecutedAnnotation(): LinkedHashMap<Int, MuteAnnotationExecuted> {
        return executedAnnotationInternal
    }
}
