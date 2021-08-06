package com.smeup.rpgparser.execution

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.file
import com.smeup.dbnative.ConnectionConfig
import com.smeup.dbnative.DBNativeAccessConfig
import com.smeup.rpgparser.interpreter.*
import com.smeup.rpgparser.jvminterop.JavaSystemInterface
import com.smeup.rpgparser.logging.defaultLoggingConfiguration
import com.smeup.rpgparser.logging.loadLogConfiguration
import com.smeup.rpgparser.parsing.ast.Api
import com.smeup.rpgparser.parsing.ast.ApiDescriptor
import com.smeup.rpgparser.parsing.ast.ApiId
import com.smeup.rpgparser.parsing.ast.CompilationUnit
import com.smeup.rpgparser.parsing.facade.Copy
import com.smeup.rpgparser.parsing.facade.CopyId
import com.smeup.rpgparser.rpginterop.*
import org.apache.commons.io.input.BOMInputStream
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashMap

class CommandLineParms internal constructor(
    val parmsList: List<String>,
    val namedParams: Map<String, Value>? = null,
    val namedParamsProducer: (compilationUnit: CompilationUnit) -> Map<String, Value>?
) {
    constructor(parmsList: List<String>) : this(
        parmsList = parmsList,
        namedParams = null,
        namedParamsProducer = { null }
    )
    constructor(namedParams: Map<String, Value>) : this(
        parmsList = emptyList(),
        namedParams = namedParams,
        namedParamsProducer = { null }
    )
    constructor(namedParamsProducer: (compilationUnit: CompilationUnit) -> Map<String, Value>) : this(
        parmsList = emptyList(),
        namedParams = null,
        namedParamsProducer = namedParamsProducer
    )
}

class CommandLineProgramNameSource(val name: String) : ProgramNameSource<CommandLineParms> {
    override fun nameFor(rpgFacade: RpgFacade<CommandLineParms>): String = name
}

class CommandLineProgram(name: String, systemInterface: SystemInterface) : RpgFacade<CommandLineParms>((CommandLineProgramNameSource(name)), systemInterface) {
    override fun toInitialValues(rpgProgram: RpgProgram, params: CommandLineParms): LinkedHashMap<String, Value> {
        val result = LinkedHashMap<String, Value> ()
        val producedNamedParams = params.namedParamsProducer.invoke(rpgProgram.cu)
        if (producedNamedParams != null) {
            result.putAll(producedNamedParams)
        } else if (params.namedParams != null) {
            result.putAll(params.namedParams)
        } else {
            val values = params.parmsList.map { parameter -> StringValue(parameter) }
            val zipped = rpgProgram.params()
                .map { dataDefinition -> dataDefinition.name }
                .zip(values)
            zipped.forEach {
                result[it.first] = it.second
            }
        }
        return result
    }

    override fun toResults(params: CommandLineParms, resultValues: LinkedHashMap<String, Value>): CommandLineParms {
        // paramsList empty and namePrams null means no params pass to jariko
        val paramsList = if (params.namedParams == null && params.parmsList.isEmpty()) {
            params.parmsList
        } else {
            resultValues.values.map { it.asString().value }
        }
        return CommandLineParms(
            parmsList = paramsList,
            namedParams = resultValues
        ) { null }
    }

    @JvmOverloads fun singleCall(parms: List<String>, configuration: Configuration = Configuration()) =
        singleCall(CommandLineParms(parms), configuration = configuration)

    @JvmOverloads fun singleCall(parms: Map<String, Value>, configuration: Configuration = Configuration()) =
        singleCall(CommandLineParms(parms), configuration = configuration)

    @JvmOverloads fun singleCall(parmsProducer: (compilationUnit: CompilationUnit) -> Map<String, Value>, configuration: Configuration = Configuration()) =
        singleCall(CommandLineParms(parmsProducer), configuration = configuration)
}

class ResourceProgramFinder(val path: String) : RpgProgramFinder {
    override fun findRpgProgram(nameOrSource: String): RpgProgram? {
        val resourceStream = ResourceProgramFinder::class.java.getResourceAsStream("$path$nameOrSource.rpgle")
        return if (resourceStream != null) {
            RpgProgram.fromInputStream(BOMInputStream(resourceStream), nameOrSource)
        } else {
            null
        }
    }

    override fun toString(): String {
        return "resource: $path"
    }

    override fun findCopy(copyId: CopyId): Copy? {
        TODO("Not yet implemented")
    }

    override fun findApiDescriptor(apiId: ApiId): ApiDescriptor? {
        TODO("Not yet implemented")
    }

    override fun findApi(apiId: ApiId): Api? {
        TODO("Not yet implemented")
    }
}

val defaultProgramFinders = listOf(
        SourceProgramFinder(),
        DirRpgProgramFinder(),
        ResourceProgramFinder("/")
)

@JvmOverloads
fun getProgram(
    nameOrSource: String,
    systemInterface: SystemInterface = JavaSystemInterface(),
    programFinders: List<RpgProgramFinder> = defaultProgramFinders
): CommandLineProgram {
    if (systemInterface is JavaSystemInterface) {
        systemInterface.rpgSystem.addProgramFinders(programFinders)
        programFinders.forEach {
            systemInterface.getAllLogHandlers().log(RpgProgramFinderLogEntry(it.toString()))
        }
    } else {
        // for compatibility with other system interfaces using singleton instance
        RpgSystem?.SINGLETON_RPG_SYSTEM?.addProgramFinders(programFinders)
        RpgSystem?.SINGLETON_RPG_SYSTEM?.log(systemInterface.getAllLogHandlers())
    }

    return CommandLineProgram(nameOrSource, systemInterface)
}

fun executePgmWithStringArgs(
    programName: String,
    programArgs: List<String>,
    logConfigurationFile: File? = null,
    programFinders: List<RpgProgramFinder> = defaultProgramFinders,
    configuration: Configuration = Configuration()
) {
    val systemInterface = JavaSystemInterface()
    systemInterface.loggingConfiguration =
        logConfigurationFile
            ?.let(::loadLogConfiguration)
            ?: defaultLoggingConfiguration()
    val commandLineProgram = getProgram(programName, systemInterface, programFinders)
    commandLineProgram.singleCall(programArgs, configuration = configuration)
}

object RunnerCLI : CliktCommand() {
    val logConfigurationFile by option("-lc", "--log-configuration").file(exists = true, readable = true)
    val programsSearchDirs by option("-psd").split(",")
    val compiledProgramDir by option("-cpd", "--compiled-program-dir").file(exists = true, readable = true)
    val programName by argument("program name")
    val programArgs by argument().multiple(required = false)
    val databasesConfigurationFile by argument("database configuration file")

    override fun run() {
        val allProgramFinders = defaultProgramFinders + (programsSearchDirs?.map { DirRpgProgramFinder(File(it)) } ?: emptyList())
        val configuration = Configuration()
        configuration.options?.compiledProgramsDir = compiledProgramDir

        // 'Reload' database configurations from properties file passed as cli argument
        loadReloadConfig(databasesConfigurationFile = databasesConfigurationFile, configuration = configuration)

        executePgmWithStringArgs(programName, programArgs, logConfigurationFile, programFinders = allProgramFinders,
        configuration = configuration)
    }
}

private fun loadReloadConfig(databasesConfigurationFile: String, configuration: Configuration) {
    if (null != RunnerCLI.databasesConfigurationFile && RunnerCLI.databasesConfigurationFile.isNotEmpty()) {
        val propertyFile = File(RunnerCLI.databasesConfigurationFile)
        require(propertyFile.exists()) {
            println("File ${RunnerCLI.databasesConfigurationFile} not exists.")
        }
        println("Load database configuration properties from ${RunnerCLI.databasesConfigurationFile}")
        val mapOfProperties = mutableMapOf<String, String>()
        val props = Properties()
        props.load(propertyFile.inputStream())
        props.forEach { (k, v) -> mapOfProperties.put(k.toString(), v.toString()) }

        val connectionsConfig = mutableListOf<ConnectionConfig>()
        val metadataPath = mapOfProperties["metadataPath"].toString()
        val url = mapOfProperties["url"].toString()
        val user = mapOfProperties["user"].toString()
        val password = mapOfProperties["password"].toString()
        val driver = mapOfProperties["driver"].toString()
        val connectionConfig = ConnectionConfig(fileName = "*", url, user, password, driver)
        connectionsConfig.add(connectionConfig)
        val dbNativeAccessConfig = DBNativeAccessConfig(connectionsConfig)
        val metadataProducer = { dbFile: String ->
            val metadataFile = File("$metadataPath$dbFile.json")
            require(metadataFile != null) {
                "Cannot find $metadataPath$dbFile.json"
            }
            FileMetadata.createInstance(metadataFile.toURI().toURL().openStream())
        }

        val reloadConfig = ReloadConfig(dbNativeAccessConfig, metadataProducer)
        configuration.reloadConfig = reloadConfig
    }
}

fun startShell() {
    SimpleShell.repl { programName, programArgs ->
        executePgmWithStringArgs(programName, programArgs)
    }
}

/**
 * This program can be used to either launch an RPG program or the shell.
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        startShell()
    } else {
        RunnerCLI.main(args)
    }
}