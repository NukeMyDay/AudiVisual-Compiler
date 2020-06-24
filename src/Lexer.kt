sealed class Token {

    // Notes
    object C: Token()
    object D: Token()
    object E: Token()
    object F: Token()
    object G: Token()
    object A: Token()
    object BF: Token()
    object B: Token()
    object CS: Token()

    // Durations
    data class NOTE_DURATION(val value: Int): Token()
    data class REST_DURATION(val value: Int): Token()

    // Octave
    data class OCTAVE(val value: Int): Token()

    // Abstracts
    object LEFT_PAREN: Token()
    object RIGHT_PAREN: Token()

    // End of File
    object END_OF_FILE: Token()

    override fun toString(): String = javaClass.simpleName
}

private class Peekable<T>(val iterator: Iterator<T>) {
    var lookahead: T? = null
    fun iterate(): T? = when {
        lookahead != null -> lookahead.also { lookahead = null }
        iterator.hasNext() -> iterator.next()
        else -> null
    }

    fun peek(): T? = iterate().also { lookahead = it }
}

class Lexer(input: String) {









    private var myInput = input.split(" ")
    private val validatedInput = mutableListOf<String>()
    private val processedInput = mutableListOf<String>()

    init {
        println("Input:\n$input")

        // We need to add preprocessed elements of myinput to validatedInput
        for(i in myInput) {
            i.trim().also{ if(it != "") validatedInput.add(it) }
        }

        println("validatedInput:\n$validatedInput")

        // We need to split abstracts and maybe bonded keywords or notes
        for(i in validatedInput) {

            when {
                i.first() == '\\' -> processedInput.add("\\").also { processedInput.add(i.replace("\\", "")) }
                i.first() == '{' -> when(i.length) {
                        1 -> processedInput.add(i)
                        else -> processedInput.add("{").also { processedInput.add(i.replace("{", "")) }
                    }
                i.last() == '}' -> when(i.length) {
                        1 -> processedInput.add(i)
                        else -> processedInput.add(i.replace("}", "")).also { processedInput.add("}") }
                    }
                else -> processedInput.add(i)
            }
        }

        processedInput.removeIf(String::isBlank)
        println("\n\nProcessed:\n$processedInput")


        for(i in processedInput) {
            val containsDigit = i.contains("[0-9]".toRegex())
            val containsOcatve = i.contains("'") or i.contains(",")



            if(containsDigit and containsOcatve or containsOcatve or containsDigit) println("Hurray ... ")


            for(c in i) {
                when {

                    c.isDigit() -> println("Hurray, $c is a digit")
                    c == '\'' -> println("Hurray, $c is a \'")
                }
            }
        }
    }




//    private val chars = Peekable(processedInput.iterator())
//    private var lookahead: Token? = null
//    private var sustained: String? = null
//
//    fun peek() = next().also { lookahead = it }
//
//    fun next(): Token {
//        val char = chars.iterate() ?: return Token.END_OF_FILE
//        return when(char && peek() != null) {
//
//            'c' -> Token.C
//            'd' -> Token.D
//            'e' -> Token.E
//            'f' -> Token.F
//            'g' -> Token.G
//            'a' -> Token.A
//            'b' -> Token.BF
//            'h' -> Token.B
//
//            '{' -> Token.LEFT_PAREN
//            '}' -> Token.RIGHT_PAREN
//
//            else -> when {
//                char.isDigit() -> throw Exception("Nummer $char")
//                else -> next()
//            }
//        }
//    }
//
//    private fun halfTone(c: Char): Token {
//        var res = c.toString()
//        while (chars.peek()?.isWhitespace() == false) res += chars.iterate()
//        return when(res) {
//            "cs" -> Token.CS
//            else -> throw Exception("Unexpected $res")
//        }
//    }
}

fun main() {
    val input = """
  \context {
    \Voice
    \override TextScript.padding = #1
    \override Glissando.thickness = #3 }
}
        \relative
        {
        \clef bass
        c2 d2 e5' f, g
        }
        {a c h cs}""".trimMargin()
    val lexer = Lexer(input)
//    while(lexer.next().also(::println) !is Token.END_OF_FILE) {}
}