package com.smeup.rpgparser.utils

import com.smeup.rpgparser.interpreter.DbField
import com.smeup.rpgparser.interpreter.FileMetadata
import com.smeup.rpgparser.interpreter.NumberType
import com.smeup.rpgparser.interpreter.StringType
import com.smeup.rpgparser.parsing.parsetreetoast.RpgType
import java.io.File

/*
 Utility to create json metadata file
 */
fun main(args: Array<String>) {
    val tableName: String = "BRARTI2L"

    val jsonMetadataFile: File = File("/home/tron/git-project/jariko/rpgJavaInterpreter-core/src/test/resources/db/metadata/$tableName.json")
    if (jsonMetadataFile.exists()) {
        println("File $jsonMetadataFile exists!")
        System.exit(1)
    }

    val recordFormat: String = "BRARTIR"
    val fields: List<DbField> = listOf(
        DbField(fieldName = "A§ARTI", StringType(15, false)),
        DbField(fieldName = "A§DEAR", StringType(35, false)),
        DbField(fieldName = "A§DEA2", StringType(35, false)),
        DbField(fieldName = "A§TIAR", StringType(5, false)),
        DbField(fieldName = "A§TPAR", StringType(3, false)),
        DbField(fieldName = "A§UNMS", StringType(2, false)),
        DbField(fieldName = "A§PESO", NumberType(entireDigits = 12, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§VOLU", NumberType(entireDigits = 12, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§CALT", StringType(15, false)),
        DbField(fieldName = "A§ARRI", StringType(15, false)),
        DbField(fieldName = "A§DISE", StringType(20, false)),
        DbField(fieldName = "A§BARC", StringType(15, false)),
        DbField(fieldName = "A§STAT", StringType(2, false)),
        DbField(fieldName = "A§LIVE", StringType(1, false)),
        DbField(fieldName = "A§GRDI", StringType(15, false)),
        DbField(fieldName = "A§GRCI", StringType(15, false)),
        DbField(fieldName = "A§NOMC", StringType(20, false)),
        DbField(fieldName = "A§CSVQ", StringType(5, false)),

        DbField(fieldName = "A§CSVA", StringType(5, false)),
        DbField(fieldName = "A§ENIR", StringType(3, false)),
        DbField(fieldName = "A§LOTR", NumberType(entireDigits = 11, decimalDigits = 3, RpgType.PACKED)),
        DbField(fieldName = "A§CONT", StringType(15, false)),
        DbField(fieldName = "A§RCDV", StringType(15, false)),
        DbField(fieldName = "A§CASI", StringType(5, false)),
        DbField(fieldName = "A§NCOM", StringType(10, false)),
        DbField(fieldName = "A§CESP", StringType(15, false)),
        DbField(fieldName = "A§CLMA", StringType(5, false)),
        DbField(fieldName = "A§CLPR", StringType(5, false)),
        DbField(fieldName = "A§CLGE", StringType(5, false)),
        DbField(fieldName = "A§CLCO", StringType(5, false)),
        DbField(fieldName = "A§CDLF", StringType(5, false)),
        DbField(fieldName = "A§CLVA", StringType(5, false)),
        DbField(fieldName = "A§CLFU", StringType(5, false)),
        DbField(fieldName = "A§CT01", StringType(12, false)),
        DbField(fieldName = "A§CT02", StringType(12, false)),
        DbField(fieldName = "A§CT03", StringType(12, false)),

        DbField(fieldName = "A§CL01", StringType(15, false)),
        DbField(fieldName = "A§CL02", StringType(15, false)),
        DbField(fieldName = "A§CL03", StringType(15, false)),
        DbField(fieldName = "A§COD1", StringType(15, false)),
        DbField(fieldName = "A§COD2", StringType(15, false)),
        DbField(fieldName = "A§COD3", StringType(15, false)),
        DbField(fieldName = "A§COD4", StringType(15, false)),
        DbField(fieldName = "A§COD5", StringType(15, false)),
        DbField(fieldName = "A§CD06", StringType(15, false)),
        DbField(fieldName = "A§CD07", StringType(15, false)),
        DbField(fieldName = "A§CD08", StringType(15, false)),
        DbField(fieldName = "A§CD09", StringType(15, false)),
        DbField(fieldName = "A§CD10", StringType(15, false)),
        DbField(fieldName = "A§NUM1", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NUM2", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NUM3", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NUM4", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NUM5", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),

        DbField(fieldName = "A§NR06", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NR07", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NR08", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NR09", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§NR10", NumberType(entireDigits = 15, decimalDigits = 5, RpgType.PACKED)),
        DbField(fieldName = "A§DT01", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT02", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT03", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT04", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT05", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT06", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT07", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT08", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT09", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§DT10", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§FL01", StringType(1, false)),
        DbField(fieldName = "A§FL02", StringType(1, false)),
        DbField(fieldName = "A§FL03", StringType(1, false)),

        DbField(fieldName = "A§FL04", StringType(1, false)),
        DbField(fieldName = "A§FL05", StringType(1, false)),
        DbField(fieldName = "A§FL06", StringType(1, false)),
        DbField(fieldName = "A§FL07", StringType(1, false)),
        DbField(fieldName = "A§FL08", StringType(1, false)),
        DbField(fieldName = "A§FL09", StringType(1, false)),
        DbField(fieldName = "A§FL10", StringType(1, false)),
        DbField(fieldName = "A§FL11", StringType(1, false)),
        DbField(fieldName = "A§FL12", StringType(1, false)),
        DbField(fieldName = "A§FL13", StringType(1, false)),
        DbField(fieldName = "A§FL14", StringType(1, false)),
        DbField(fieldName = "A§FL15", StringType(1, false)),
        DbField(fieldName = "A§FL16", StringType(1, false)),
        DbField(fieldName = "A§FL17", StringType(1, false)),
        DbField(fieldName = "A§FL18", StringType(1, false)),
        DbField(fieldName = "A§FL19", StringType(1, false)),
        DbField(fieldName = "A§FL20", StringType(1, false)),
        DbField(fieldName = "A§FL21", StringType(1, false)),

        DbField(fieldName = "A§FL22", StringType(1, false)),
        DbField(fieldName = "A§FL23", StringType(1, false)),
        DbField(fieldName = "A§FL24", StringType(1, false)),
        DbField(fieldName = "A§FL25", StringType(1, false)),
        DbField(fieldName = "A§FL26", StringType(1, false)),
        DbField(fieldName = "A§FL27", StringType(1, false)),
        DbField(fieldName = "A§FL28", StringType(1, false)),
        DbField(fieldName = "A§FL29", StringType(1, false)),
        DbField(fieldName = "A§FL30", StringType(1, false)),
        DbField(fieldName = "A§FL31", StringType(1, false)),
        DbField(fieldName = "A§FL32", StringType(1, false)),
        DbField(fieldName = "A§FL33", StringType(1, false)),
        DbField(fieldName = "A§FL34", StringType(1, false)),
        DbField(fieldName = "A§FL35", StringType(1, false)),
        DbField(fieldName = "A§FL36", StringType(1, false)),
        DbField(fieldName = "A§FL37", StringType(1, false)),
        DbField(fieldName = "A§FL38", StringType(1, false)),
        DbField(fieldName = "A§FL39", StringType(1, false)),

        DbField(fieldName = "A§FL40", StringType(1, false)),
        DbField(fieldName = "A§USIN", StringType(10, false)),
        DbField(fieldName = "A§DTIN", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§ORIN", NumberType(entireDigits = 6, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§USAG", StringType(10, false)),
        DbField(fieldName = "A§DTAG", NumberType(entireDigits = 8, decimalDigits = 0, RpgType.PACKED)),
        DbField(fieldName = "A§ORAG", NumberType(entireDigits = 6, decimalDigits = 0, RpgType.PACKED))
    )

    val accessField: List<String> = listOf("A§TIAR", "A§ARTI")
    val fileMetadata: FileMetadata = FileMetadata(tableName, recordFormat, fields, accessField)

    val jsonContent: String = fileMetadata.toJson()
    jsonMetadataFile.writeText(jsonContent)
}