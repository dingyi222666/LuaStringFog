package io.github.dingyi222666.io.github.dingyi222666.luastringfog

import io.github.dingyi222666.luaparser.lexer.LuaLexer
import io.github.dingyi222666.luaparser.lexer.LuaTokenTypes
import io.github.dingyi222666.luaparser.util.parseLuaString

fun stringFog1(code: String): String {
    val lexer = LuaLexer(code)

    val buffer = StringBuilder()

    for ((token, tokenText) in lexer) {
        if (token === LuaTokenTypes.STRING || token === LuaTokenTypes.LONG_STRING) {
            buffer.append(getObfuscationString1(tokenText))
        } else {
            buffer.append(tokenText)
        }
    }

    return buffer.toString()
}

fun getObfuscationString1(string: String): String {
    val decodedText = parseLuaString(string)

    return decodedText.map {
        it.code
    }.joinToString(prefix = "utf8.char(", postfix = ")", separator = ",")
}

fun stringFog2(code: String): String {
    val lexer = LuaLexer(code)

    val buffer = StringBuilder()

    for ((token, tokenText) in lexer) {
        if (token === LuaTokenTypes.STRING || token === LuaTokenTypes.LONG_STRING) {
            buffer.append(getObfuscationString2(tokenText))
        } else {
            buffer.append(tokenText)
        }
    }

    return buffer.toString()
}

fun getObfuscationString2(string: String): String {
    val decodedText = parseLuaString(string)

    val codeList = decodedText.map {
        it.code
    }

    if (codeList.size < 60) {
        return codeList.joinToString(prefix = "utf8.char(", postfix = ")", separator = ",")
    }

    val buffer = StringBuilder("table.concat({")


    codeList.chunked(decodedText.length / 60).forEach {
        buffer.append(it.joinToString(prefix = "utf8.char(", postfix = ")", separator = ","))
        buffer.append(",")
    }

    // delete ,
    buffer.deleteCharAt(buffer.length - 1)

    buffer.append("})")

    return buffer.toString()
}


fun stringFog3(code: String): String {
    val lexer = LuaLexer(code)

    val buffer = StringBuilder()

    for ((token, tokenText) in lexer) {
        if (token === LuaTokenTypes.STRING || token === LuaTokenTypes.LONG_STRING) {
            buffer.append(getObfuscationString3(tokenText))
        } else {
            buffer.append(tokenText)
        }
    }

    return buffer.toString()
}

fun getObfuscationString3(string: String): String {
    val decodedText = parseLuaString(string)

    val codeList = decodedText.map {
        it.code
    }

    if (codeList.size < 60) {
        return codeList.joinToString(prefix = "utf8.char(", postfix = ")", separator = ",")
    }

    val buffer = StringBuilder("(function()")

    buffer.append("local tab = {}\n")

    codeList.chunked(decodedText.length / 60).forEach {
        buffer.append("table.insert(tab, ")
        buffer.append(it.joinToString(prefix = "utf8.char(", postfix = ")", separator = ","))
        buffer.append(");")
    }


    buffer.append("return table.concat(tab)\nend)()")

    return buffer.toString()
}


class StringPool {
    private val pool = mutableListOf<Pair<String, List<Int>>>()

    private fun getOrPutCodeList(string: String, list: List<Int>) {
        if (!pool.any { it.first == string }) {
            pool.add(Pair(string, list))
        }

    }

    fun getObfuscationString(string: String): String {
        val parsedString = parseLuaString(string)
        val chunkList = parsedString.chunked(30)

        chunkList.forEach { chunkString ->
            getOrPutCodeList(chunkString, chunkString.map { char -> char.code })
        }


        if (chunkList.size == 1) {
            val first = chunkList[0]
            val codeListOfIndex = pool.indexOfLast { it.first == first }

            return "utf8.char(table.unpack(string_pool[${codeListOfIndex}]))"
        }

        val buffer = StringBuilder("(function()")

        buffer.append("local tab = {}\n")

        chunkList.forEach { chunkString ->
            val codeListOfIndex = pool.indexOfLast { it.first == chunkString }

            if (codeListOfIndex == -1) {
                throw Exception("string not found")
            }
            buffer.append("table.insert(tab, utf8.char(table.unpack(string_pool[${codeListOfIndex}])));")
        }

        buffer.append("return table.concat(tab)\nend)()")

        return buffer.toString()
    }

    fun formatToTable(): String {
        val buffer = StringBuilder("local string_pool = {\n")

        pool.forEachIndexed { index, pair ->
            buffer.append("  [${index}] = {")
            buffer.append(pair.second.joinToString(prefix = "", postfix = "", separator = ","))
            buffer.append("},\n")
        }

        buffer.append("};\n")

        return buffer.toString()
    }
}


fun stringFog4(code: String): String {
    val lexer = LuaLexer(code)

    val buffer = StringBuilder()

    val pool = StringPool()

    for ((token, tokenText) in lexer) {
        if (token === LuaTokenTypes.STRING || token === LuaTokenTypes.LONG_STRING) {
            buffer.append(pool.getObfuscationString(tokenText))
        } else {
            buffer.append(tokenText)
        }
    }


    buffer.insert(0, pool.formatToTable())

    return buffer.toString()
}


class StringPool2 {
    private val pool = mutableListOf<Pair<String, List<Int>>>()

    private fun getOrPutCodeList(string: String, list: List<Int>) {
        if (!pool.any { it.first == string }) {
            pool.add(Pair(string, list))
        }

    }

    fun getObfuscationString(string: String): String {
        val parsedString = parseLuaString(string)
        val chunkList = parsedString.chunked(30)

        chunkList.forEach { chunkString ->
            getOrPutCodeList(chunkString, chunkString.map { char -> char.code })
        }


        if (chunkList.size == 1) {
            val first = chunkList[0]
            val codeListOfIndex = pool.indexOfLast { it.first == first }

            return "utf8.char(table.unpack(string_pool[${codeListOfIndex}]))"
        }

        val buffer = StringBuilder("(function()")

        buffer.append("local tab = {}\n")

        chunkList.forEach { chunkString ->
            val codeListOfIndex = pool.indexOfLast { it.first == chunkString }

            if (codeListOfIndex == -1) {
                throw Exception("string not found")
            }
            buffer.append("table.insert(tab, utf8.char(table.unpack(string_pool[${codeListOfIndex}])));")
        }

        buffer.append("return table.concat(tab)\nend)()")

        return buffer.toString()
    }

    fun formatToTable(): String {
        val buffer = StringBuilder("local int_pool = {\n")

        // <code,<index,code>>
        val mapped = mutableMapOf<Int, Pair<Int, Int>>()
        var mappedIndex = 0
        pool.map { it.second }.forEach { codeList ->

            codeList.forEach { code ->
                if (!mapped.containsKey(code)) {
                    mapped[code] = (mappedIndex++ to code)
                }
            }
        }

        mapped.forEach {
            buffer.append("  [${it.value.first}] = ${it.value.second},")
        }

        buffer.append("};\n")

        val buffer1 = StringBuilder("local string_pool = {\n")

        pool.forEachIndexed { index, pair ->
            buffer1.append("  [${index}] = {")
            buffer1.append(
                pair.second.joinToString(prefix = "", postfix = "", separator = ",") {
                    "int_pool[${mapped[it]?.first}]"
                })
            buffer1.append("},\n")
        }

        buffer1.append("};\n")

        buffer1.insert(0, buffer)

        return buffer1.toString()
    }
}


fun stringFog5(code: String): String {
    val lexer = LuaLexer(code)

    val buffer = StringBuilder()

    val pool = StringPool2()

    for ((token, tokenText) in lexer) {
        if (token === LuaTokenTypes.STRING || token === LuaTokenTypes.LONG_STRING) {
            buffer.append(pool.getObfuscationString(tokenText))
        } else {
            buffer.append(tokenText)
        }
    }


    buffer.insert(0, pool.formatToTable())

    return buffer.toString()
}


class StringPool3 {
    private val pool = mutableListOf<Triple<String, List<Int>, Int>>()

    private fun getOrPutCodeList(string: String, list: List<Int>) {
        if (pool.any { it.first == string }) {
            return
        }
        val key = randomNumber(100, 10000).toInt()
        pool.add(Triple(string, encryptNumberList(list, key), key))
    }

    private fun randomNumber(min: Int, max: Int): Double {
        return (Math.random() * (max - min + 1)) + min
    }

    private fun encryptNumberList(list: List<Int>, key: Int): List<Int> {
        // xor
        return list.map { it.xor(key) }
    }

    fun getObfuscationString(string: String): String {
        val parsedString = parseLuaString(string)
        val chunkList = parsedString.chunked(30)

        chunkList.forEach { chunkString ->
            getOrPutCodeList(chunkString, chunkString.map { char -> char.code })
        }


        if (chunkList.size == 1) {
            val first = chunkList[0]
            val codeIndex = pool.find { it.first == first }
            val index = pool.indexOf(codeIndex)

            return "utf8.char(table.unpack(xor(string_pool[${index}],${codeIndex?.third})))"
        }

        val buffer = StringBuilder("(function()")

        buffer.append("local tab = {}\n")

        chunkList.forEach { chunkString ->
            val codeIndex = pool.find { it.first == chunkString }
            val codeListOfIndex = pool.indexOf(codeIndex)

            if (codeListOfIndex == -1) {
                throw Exception("string not found")
            }
            buffer.append("table.insert(tab, utf8.char(table.unpack(xor(string_pool[${codeListOfIndex}],${codeIndex?.third}))));")
        }

        buffer.append("return table.concat(tab)\nend)()")

        return buffer.toString()
    }


    fun formatToTable(): String {
        val xor = """
            local xor = function (array, key) local result = {} for i = 1, #array do result[i] = array[i] ~ key end return result end
        """.trimIndent()
        val buffer = StringBuilder(xor).append("\nlocal int_pool = {\n")

        // <code,<index,code>>
        val mapped = mutableMapOf<Int, Pair<Int, Int>>()
        var mappedIndex = 0
        pool.map { it.second }.forEach { codeList ->
            codeList.forEach { code ->
                if (!mapped.containsKey(code)) {
                    mapped[code] = (mappedIndex++ to code)
                }
            }
        }

        mapped.forEach {
            buffer.append("  [${it.value.first}] = ${it.value.second},")
        }

        buffer.append("};\n")

        val buffer1 = StringBuilder("local string_pool = {\n")

        pool.forEachIndexed { index, pair ->
            buffer1.append("  [${index}] = {")
            buffer1.append(
                pair.second.joinToString(prefix = "", postfix = "", separator = ",") {
                    "int_pool[${mapped[it]?.first}]"
                })
            buffer1.append("},\n")
        }

        buffer1.append("};\n")

        buffer1.insert(0, buffer)

        return buffer1.toString()
    }
}


fun stringFog6(code: String): String {
    val lexer = LuaLexer(code)

    val buffer = StringBuilder()

    val pool = StringPool3()

    for ((token, tokenText) in lexer) {
        if (token === LuaTokenTypes.STRING || token === LuaTokenTypes.LONG_STRING) {
            buffer.append(pool.getObfuscationString(tokenText))
        } else {
            buffer.append(tokenText)
        }
    }


    buffer.insert(0, pool.formatToTable())

    return buffer.toString()
}


fun stringFog7(code: String, globalVar: List<String>): String {
    val lexer = LuaLexer(code)

    val buffer = StringBuilder()

    val pool = StringPool3()

    val queue = ArrayDeque<Pair<LuaTokenTypes, String>>()

    var canObfuscation = true

    for ((token, tokenText) in lexer) {
        if (
            (token == LuaTokenTypes.STRING || token == LuaTokenTypes.LONG_STRING)
        ) {
            if (queue.lastOrNull()?.first == LuaTokenTypes.NAME) {
                buffer.append("(")
                buffer.append(pool.getObfuscationString(tokenText))
                buffer.append(")")
            } else {
                buffer.append(pool.getObfuscationString(tokenText))
            }

        } else if (token == LuaTokenTypes.NAME) {
            if (canObfuscation && globalVar.contains(tokenText) && queue.lastOrNull()?.first != LuaTokenTypes.DOT) {
                buffer.append("_G[")
                buffer.append(pool.getObfuscationString(tokenText))
                buffer.append("]")

                queue.addLast(token to tokenText)
                continue
            }

            if (canObfuscation && queue.lastOrNull()?.first == LuaTokenTypes.DOT) {
                buffer.deleteCharAt(buffer.lastIndexOf(".")) // .
                buffer.append("[")
                buffer.append(pool.getObfuscationString(tokenText))
                buffer.append("]")

                queue.addLast(token to tokenText)
                continue
            }

            buffer.append(tokenText)
        } else {

            canObfuscation = when (token) {
                LuaTokenTypes.FUNCTION -> {
                    false
                }

                LuaTokenTypes.LPAREN -> {
                    true
                }

                LuaTokenTypes.RPAREN -> {
                    true
                }

                LuaTokenTypes.NEW_LINE -> {
                    true
                }

                else -> {
                    canObfuscation
                }
            }

            buffer.append(tokenText)
        }

        if (token != LuaTokenTypes.WHITE_SPACE && token != LuaTokenTypes.NEW_LINE) {
            queue.addLast(token to tokenText)
        }
    }


    buffer.insert(0, pool.formatToTable())

    return buffer.toString()
}
