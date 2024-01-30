package com.smeup.rpgparser.interpreter

import com.smeup.rpgparser.utils.isZero
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

import com.smeup.rpgparser.interpreter.DecEdit.*

internal fun DecimalValue.formatAs(format: String, type: Type, decedit: DecEdit, padChar: Char = ' '): StringValue {
    // %EDITC documentation: https://www.ibm.com/docs/en/i/7.3?topic=80-edtcde-edit-code-keyword-display-files

    // The total length of the %EDITC resulting string is calculated as follows:
    //      length of the field (no matter how many decimal there are)
    //    + potential number of separators (thousands and decimal) even if the real length of the decimal doesn't require separators
    //    + if sign is displayed: number of char of the sign on the right even if it's positive
    // For example: if we assign 1234567 in a field defined "7S 0" with edit code 1
    //     the resulting string will be: '1,234,567' where:
    //     - the total length will be 9
    //     - the field length will be 7: 7 chars for the integer part (1234567) + 0 chars for the decimal part
    //     - the separators to include in the calculation of the length in this case will be 2
    //     - the sign length will be 0

    // String representation anomalies: https://www.mcpressonline.com/programming/rpg/converting-numeric-to-character-and-character-to-numeric

    // Notes about anomalies:
    // - If the amount is too big for the defined field it must be trunked on the left must be trunked on the left
    //   for integer part and on the right for the decimal part.
    //     For example: if we assign -123456.239 in a field defined "7S 2" with edit code A,
    //     the resulting string will be: '34,567.23CR' where:
    //     - the total length will be 11
    //     - the field length will be 7: 5 chars for the integer part (34567) + 2 chars for the decimal part (23)
    //     - the separators to include in the calculation of the length in this case will be only 2
    //     - the sign length will be 2
    // - The spaces reserved for the sign, must be padded at the end of the returned string, while the rest
    // (length of the field + potential separators + edit code char) must be padded at the beginning of the returned string
    //
    // As we can see, the same field defined as numeric 7 might have different resulting string

    var cfgCommasDisplayed = false
    var cfgDecimalPointDisplayed = false
    var cfgSign = ""
    var cfgBlankValueOfZero = false
    var cfgLeadingZeroSuppress = false
    var cfgPadChar = padChar
    var cfgSignPosition = true // true = begin, false = end

    val t = (type as NumberType)

    var wrkTotalLength = 0
    val decValue = this.value

    var retValue = ""

    fun thousandSeparators(): Int {
        val quotient = t.entireDigits / 3
        val reminder = t.entireDigits % 3
        val ts = if (reminder == 0) quotient - 1 else quotient
        return if (ts < 0) 0 else ts
    }

    fun decimalSeparators() = if (t.decimalDigits > 0) 1 else 0

    fun getWrkTotalLength(): Int {
        var tot = 0
        if (cfgCommasDisplayed) {
            tot += thousandSeparators()
        }
        if (cfgDecimalPointDisplayed) {
            tot += decimalSeparators()
        }
        tot += if (cfgSign.isNotEmpty()) cfgSign.length else 0
        tot += t.decimalDigits + t.entireDigits
        return tot
    }

    fun setCfg() {
        when (format) {
            "1" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = ""
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "2" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = ""
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "3" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = ""
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "4" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = ""
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "A" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = "CR"
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "B" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = "CR"
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "C" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = "CR"
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "D" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = "CR"
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "J" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "K" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "L" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "M" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "N" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = true
            }

            "O" -> {
                cfgCommasDisplayed = true
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = true
            }
            "P" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = true
            }
            "Q" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = true
                cfgSign = "-"
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = true
            }
            "X" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = false
                cfgSign = ""
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = false
                cfgSignPosition = false
                cfgPadChar = '0'
            }
            "Y" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = false
                cfgSign = ""
                cfgBlankValueOfZero = false
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
            "Z" -> {
                cfgCommasDisplayed = false
                cfgDecimalPointDisplayed = false
                cfgSign = ""
                cfgBlankValueOfZero = true
                cfgLeadingZeroSuppress = true
                cfgSignPosition = false
            }
        }
    }

    fun getDecimalSeparator(): String {
        return when (decedit) {
            DOT, ZERO_DOT -> "."
            COMMA, ZERO_COMMA -> ","
            else -> ""
        }
    }

    fun standardDecimalFormat(type: NumberType, locale: Locale) =
        DecimalFormat(decimalPattern(type), DecimalFormatSymbolsRepository.getSymbols(locale)).format(decValue.abs())

    fun getStandardFormat(): String {
        return when (decedit) {
            COMMA -> {
                standardDecimalFormat(type, Locale.ITALY)
            }
            ZERO_COMMA -> {
                if (decValue.abs() < BigDecimal.ONE) {
                    buildString {
                        append("0")
                        append(standardDecimalFormat(type, Locale.ITALY))
                    }
                } else {
                    standardDecimalFormat(type, Locale.ITALY)
                }
            }
            ZERO_DOT -> {
                if (decValue.abs() < BigDecimal.ONE) {
                    buildString {
                        append("0")
                        append(standardDecimalFormat(type, Locale.US))
                    }
                } else {
                    standardDecimalFormat(type, Locale.US)
                }
            }
            DOT -> {
                standardDecimalFormat(type, Locale.US)
            }
        }
    }

    fun italianDecimalFormatWithNoThousandsSeparator(type: NumberType) =
        DecimalFormat(buildString { append("#"); append(decimalsFormatString(type)) }, DecimalFormatSymbolsRepository.italianSymbols).format(decValue.abs())

    fun usDecimalFormatWithNoThousandsSeparator(type: NumberType) =
        DecimalFormat(buildString { append("#"); append(decimalsFormatString(type)) }, DecimalFormatSymbolsRepository.usSymbols).format(decValue.abs())

    fun getItalianFormat(): String {
        return when (decedit) {
            COMMA -> {
                italianDecimalFormatWithNoThousandsSeparator(type)
            }
            ZERO_COMMA -> {
                if (decValue.abs() < BigDecimal.ONE) {
                    buildString {
                        append("0")
                        append(standardDecimalFormat(type, Locale.ITALY))
                    }
                } else {
                    italianDecimalFormatWithNoThousandsSeparator(type)
                }
            }
            ZERO_DOT -> {
                if (decValue.abs() < BigDecimal.ONE) {
                    buildString {
                        append("0")
                        append(standardDecimalFormat(type, Locale.US))
                    }
                } else {
                    usDecimalFormatWithNoThousandsSeparator(type)
                }
            }
            DOT -> {
                DecimalFormat(buildString {
                    append("#")
                    append(decimalsFormatString(type))
                }, DecimalFormatSymbolsRepository.usSymbols)
                    .format(decValue.abs())
            }
        }
    }

    fun removeLeadingZeros() {
        if (decedit == DOT || decedit == COMMA) {
            val workValue = retValue
            if (cfgLeadingZeroSuppress) {
                var exec = false
                for (i in 0 until workValue.length) {
                    if (workValue[i] != '0') {
                        retValue = workValue.substring(i)
                        exec = true
                        break
                    }
                }
                if (!exec) {
                    retValue = "0"
                }
            }
        }
    }

    // The functions below correspond to the EDITC parameter, one function per value
    fun toBlank(c: Char) = if (c == '0') ' ' else c

    fun appendSign() {
        if (cfgSign.isNotEmpty()) {
            if (decValue < ZERO) {
                if (cfgSignPosition) {
                    retValue = cfgSign + retValue
                } else {
                    retValue += cfgSign
                }
            } else {
                if (cfgSignPosition) {
                    retValue = cfgPadChar.toString().repeat(cfgSign.length) + retValue
                } else {
                    retValue += cfgPadChar.toString().repeat(cfgSign.length)
                }
            }
        }
    }

    fun removeDecimalSeparator() {
        if (!cfgDecimalPointDisplayed) {
            val decimalSeparator = getDecimalSeparator()
            retValue = retValue.replace(decimalSeparator, "")
        }
    }

    fun addPadding() {
        retValue = retValue.padStart(wrkTotalLength, cfgPadChar)
    }

    fun replaceZeroWithBlank() {
        if (cfgBlankValueOfZero && decValue.isZero())
            retValue = " ".repeat(wrkTotalLength)
    }

    fun f1(): String {
        // parse to local format
        if (cfgCommasDisplayed) {
            retValue = getStandardFormat()
        } else {
            retValue = getItalianFormat()
        }

        // get total length
        wrkTotalLength = getWrkTotalLength()

        // append sign
        appendSign()

        // remove the decimal separator
        removeDecimalSeparator()

        // suppress leading zeros
        removeLeadingZeros()

        // padding start
        addPadding()

        // replace 0 with blank
        replaceZeroWithBlank()

        return retValue
    }

    fun f2(): String {
        return f1()
    }

    fun f3(): String {
        return f1()
    }

    fun f4(): String {
        return f1()
    }

    fun fA(): String {
        return f1()
    }

    fun fB(): String {
        return f1()
    }

    fun fC(): String {
        return f1()
    }

    fun fD(): String {
        return f1()
    }

    fun fJ(): String {
        return f1()
    }

    fun fK(): String {
        return f1()
    }

    fun fL(): String {
        return f1()
    }

    fun fM(): String {
        return f1()
    }

    fun fN(): String {
        return f1()
    }

    fun fO(): String {
        return f1()
    }

    fun fP(): String {
        return f1()
    }

    fun fQ(): String {
        return f1()
    }

    fun fX(): String {
        retValue = f1()
        if (decValue < ZERO) {
            throw UnsupportedOperationException("Unsupported format for %EDITC: $format with negative values")
        }
        return retValue
    }

    fun fY(): String {
        // get total length
        wrkTotalLength = getWrkTotalLength()

        var stringN = decValue.abs().unscaledValue().toString().trim()

        val testLen =
        if (decValue.isZero()) {
            wrkTotalLength
        } else {
            stringN.length
        }

        if (testLen <= 2) {
            throw UnsupportedOperationException("Unsupported format for %EDITC: $format with value length < 3")
        } else if (testLen == 3) {
            // "nn⁄n"
            stringN = stringN.padStart(3, '0')
            stringN = "${toBlank(stringN[0])}${stringN[1]}/${stringN[2]}".padStart(wrkTotalLength + 2)
        } else if (testLen == 4) {
            // "nn⁄nn"
            stringN = stringN.padStart(4, '0')
            stringN = "${toBlank(stringN[0])}${stringN[1]}/${stringN[2]}${stringN[3]}".padStart(wrkTotalLength + 2)
        } else if (testLen == 5) {
            // "nn⁄nn⁄n"
            stringN = stringN.padStart(5, '0')
            stringN = "${toBlank(stringN[0])}${stringN[1]}/${stringN[2]}${stringN[3]}/${stringN[4]}".padStart(wrkTotalLength + 2)
        } else if (testLen == 6) {
            // "nn⁄nn⁄nn"
            stringN = stringN.padStart(6, '0')
            stringN = "${toBlank(stringN[0])}${stringN[1]}/${stringN[2]}${stringN[3]}/${stringN[4]}${stringN[5]}".padStart(wrkTotalLength + 2)
        } else if (testLen == 7) {
            // "nnn⁄nn⁄nn"
            stringN = stringN.padStart(7, '0')
            stringN = "${toBlank(stringN[0])}${stringN[1]}${stringN[2]}/${stringN[3]}${stringN[4]}/${stringN[5]}${stringN[6]}".padStart(wrkTotalLength + 2)
        } else if (testLen == 8) {
            // "nnn⁄nn⁄nn"
            stringN = stringN.padStart(8, '0')
            stringN = "${toBlank(stringN[0])}${stringN[1]}/${stringN[2]}${stringN[3]}/${stringN[4]}${stringN[5]}${stringN[6]}${stringN[7]}".padStart(wrkTotalLength + 2)
        } else if (testLen == 9) {
            // "nnn⁄nn⁄nnnn"
            stringN = stringN.padStart(9, '0')
            stringN = "${toBlank(stringN[0])}${stringN[1]}${stringN[2]}/${stringN[3]}${stringN[4]}/${stringN[5]}${stringN[6]}${stringN[7]}${stringN[8]}".padStart(wrkTotalLength + 2)
        } else {
            throw UnsupportedOperationException("Unsupported format for %EDITC: $format with value length > 9")
        }
        return stringN
    }

    fun fZ(): String {
        return f1()
    }

    // **************
    // starts here !!
    // **************

    // set edit code configuration
    setCfg()

    return when (format) {
        "1" -> StringValue(f1())
        "2" -> StringValue(f2())
        "3" -> StringValue(f3())
        "4" -> StringValue(f4())
        "A" -> StringValue(fA())
        "B" -> StringValue(fB())
        "C" -> StringValue(fC())
        "D" -> StringValue(fD())
        "X" -> StringValue(fX())
        "J" -> StringValue(fJ())
        "K" -> StringValue(fK())
        "L" -> StringValue(fL())
        "M" -> StringValue(fM())
        "N" -> StringValue(fN())
        "O" -> StringValue(fO())
        "P" -> StringValue(fP())
        "Q" -> StringValue(fQ())
        "Y" -> StringValue(fY())
        "Z" -> StringValue(fZ())
        else -> throw UnsupportedOperationException("Unsupported format for %EDITC: $format")
    }
}

internal fun DecimalValue.formatAsWord(format: String, type: Type): StringValue {
    fun isConst(formatChar: Char): Boolean =
        when (formatChar) {
            '0', '*' -> false // TODO
            ' ' -> false // TODO see if it's OK
            else -> true
        }

    fun CharArray.cleanZeroesUntil(lastPosition: Int): CharArray {
        loop@ for (i in 0..lastPosition) {
            if (this[i] == '0') {
                this[i] = ' '
            } else if (this[i] in '1'..'9') {
                break@loop
            }
        }
        return this
    }

    fun String.handleSignum(decimalValue: DecimalValue): String =
        if (!decimalValue.isPositive() || this.count { it == '-' } > 1) {
            this
        } else {
            this.replaceFirst("-", " ")
        }

    if (type !is NumberType) throw UnsupportedOperationException("Unsupported type for %EDITW: $type")
    val firstZeroInFormat = format.indexOfFirst { it == '0' }
    val wholeNumberAsString =
        this.significantDigitsAsStringJustDigits(type)
            .padStart(format.length)
            .mapIndexed { i, c -> if ((firstZeroInFormat > -1 && i > firstZeroInFormat) && c == ' ') '0' else c }
            .reversed()
            .iterator()
    val reversedResult = " ".repeat(format.length).toCharArray()
    format.reversed().forEachIndexed {
        i, formatChar ->
        if (isConst(formatChar)) {
            reversedResult[i] = formatChar
        } else {
            if (wholeNumberAsString.hasNext()) {
                reversedResult[i] = wholeNumberAsString.next()
            }
        }
    }
    val result =
        reversedResult
        .reversedArray()
        .cleanZeroesUntil(firstZeroInFormat)
        .joinToString(separator = "")
        .handleSignum(this)

    return StringValue(result)
}

object DecimalFormatSymbolsRepository {
    val italianSymbols = DecimalFormatSymbols(Locale.ITALY)
    val usSymbols = DecimalFormatSymbols(Locale.US)
    fun getSymbols(locale: Locale): DecimalFormatSymbols {
        return when (locale) {
            Locale.ITALY -> italianSymbols
            Locale.US -> usSymbols
            else -> DecimalFormatSymbols(locale)
        }
    }
}

private fun decimalPattern(type: NumberType) = buildString {
    append("#,###")
    append(decimalsFormatString(type))
}

fun decimalsFormatString(t: NumberType) =
    if (t.decimalDigits == 0) {
        ""
    } else buildString {
        append(".")
        append("".padEnd(t.decimalDigits, '0'))
    }

fun DecimalValue.significantDigitsAsStringJustDigits(t: NumberType): String = significantDigitsAsString(t).filter(Char::isDigit)
fun DecimalValue.significantDigitsAsString(t: NumberType): String = DecimalFormat(decimalsFormatString(t)).format(this.value)
