package com.smeup.rpgparser

import com.smeup.dbnative.model.FileMetadata
import com.smeup.rpgparser.execution.*
import com.smeup.rpgparser.interpreter.*
import com.smeup.rpgparser.jvminterop.JavaSystemInterface
import com.smeup.rpgparser.parsing.ast.CompilationUnit
import com.smeup.rpgparser.rpginterop.DirRpgProgramFinder
import com.smeup.rpgparser.rpginterop.RpgProgramFinder
import java.io.File

/**
 * This class must be extended from all test classes in order to automatically manage tests using both version
 * of the compiled programs and non-compiled programs.
 * For each test case you have to implement a base test case (YourTest : AbstractTest()) and then you'll implement
 * (YourTestCompiled : YourTest()) that simply it will override useCompiledVersion method returning true
 * */
abstract class AbstractTest {

    open fun assertASTCanBeProduced(
        exampleName: String,
        considerPosition: Boolean = false,
        withMuteSupport: Boolean = false,
        printTree: Boolean = false
    ): CompilationUnit {
        return assertASTCanBeProduced(
            exampleName = exampleName,
            considerPosition = considerPosition,
            withMuteSupport = withMuteSupport,
            printTree = printTree,
            compiledProgramsDir = getTestCompileDir()
        )
    }

    fun outputOf(
        programName: String,
        initialValues: Map<String, Value> = mapOf(),
        printTree: Boolean = false,
        si: CollectorSystemInterface = ExtendedCollectorSystemInterface()
    ): List<String> {
        return outputOf(
            programName = programName,
            initialValues = initialValues,
            printTree = printTree,
            si = si,
            compiledProgramsDir = getTestCompileDir()
        )
    }

    fun execute(
        programName: String,
        initialValues: Map<String, Value>,
        si: CollectorSystemInterface = ExtendedCollectorSystemInterface(),
        logHandlers: List<InterpreterLogHandler> = SimpleLogHandler.fromFlag(TRACE),
        printTree: Boolean = false
    ): InternalInterpreter {
        return execute(
            programName = programName,
            initialValues = initialValues,
            si = si,
            logHandlers = logHandlers,
            printTree = printTree,
            compiledProgramsDir = getTestCompileDir()
        )
    }

    fun outputOfDBPgm(
        programName: String,
        metadata: List<FileMetadata> = emptyList(),
        initialSQL: List<String> = emptyList(),
        inputParms: Map<String, Value> = mapOf()
    ): List<String> {
        return com.smeup.rpgparser.db.utilities.outputOfDBPgm(
            programName = programName,
            metadata = metadata,
            initialSQL = initialSQL,
            inputParms = inputParms
        )
    }

    fun executePgmWithStringArgs(
        programName: String,
        programArgs: List<String>,
        logConfigurationFile: File? = null,
        programFinders: List<RpgProgramFinder> = defaultProgramFinders,
        configuration: Configuration = Configuration()
    ) {
        com.smeup.rpgparser.execution.executePgmWithStringArgs(
            programName = programName,
            programArgs = programArgs,
            logConfigurationFile = logConfigurationFile,
            programFinders = programFinders,
            configuration = configuration.adaptForTestCase(this)
        )
    }

    /**
     * Execute a PGM
     * @param programName Name or relative path followed by name. Example performance/MUTE10_01 to execute a PGM
     * in test/resources/performance/MUTE10_01.rpgle
     * @param params If needed, an instance of params to pass to the program. Default empty params
     * @param configuration If needed, you can pass an instance of configuration. Default empty configuration
     * @param systemInterface If needed, you can pass an instance of SystemInterface. Default JavaSystemInterface
     * */
    fun executePgm(
        programName: String,
        params: CommandLineParms = CommandLineParms(emptyList()),
        configuration: Configuration,
        systemInterface: SystemInterface = JavaSystemInterface()
    ): CommandLineParms? {
        val resourceName = if (programName.endsWith(".rpgle")) {
            programName
        } else {
            "$programName.rpgle"
        }
        val resource = javaClass.getResource("/$resourceName")
        require(resource != null) {
            "Cannot find resource $resourceName"
        }
        val programFinders = listOf(DirRpgProgramFinder(directory = File(resource.path).parentFile.parentFile))
        val jariko = getProgram(
            nameOrSource = programName,
            systemInterface = systemInterface,
            programFinders = programFinders
        )
        return jariko.singleCall(params, configuration.adaptForTestCase(this))
    }

    fun getTestCompileDir(): File? {
        return if (useCompiledVersion()) {
            testCompiledDir
        } else {
            null
        }
    }

    open fun useCompiledVersion() = false
}

fun Configuration.adaptForTestCase(testCase: AbstractTest): Configuration {
    if (this.options != null) {
        this.options!!.compiledProgramsDir = testCase.getTestCompileDir()
    } else {
        this.options = Options(compiledProgramsDir = testCase.getTestCompileDir())
    }
    return this
}