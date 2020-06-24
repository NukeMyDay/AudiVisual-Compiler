sealed class Token {

    // Notes
    object C: Token()
    object D: Token()
    object E: Token()
    object F: Token()
    object G: Token()
    object A: Token()
    object H: Token()

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
            when(i.replace("[s0-9\',]".toRegex(), "")) {
                "c" -> validate(i).also { Token.C.also(::println) }
                "d" -> validate(i).also { Token.D.also(::println) }
                "f" -> validate(i).also { Token.F.also(::println) }
                "h" -> validate(i).also { Token.H.also(::println) }
                "e" -> validate(i).also { Token.E.also(::println) }
                "a" -> validate(i).also { Token.A.also(::println) }
                "g" -> validate(i).also { Token.G.also(::println) }
                else -> println(i)
            }
        }
    }

    private fun validate(input: String) {
        if (input.contains("[0-9]".toRegex())) validateDuration(input)
        if (input.contains("[\',]".toRegex())) validateOctave(input)
    }

    private fun validateDuration(input: String): Token {
         return Token.NOTE_DURATION(input.replace("[cdefgah\',]".toRegex(), "").toInt()).also(::println)
    }

    private fun validateOctave(input: String): Token {
        var value = 0
        when (input.replace("[cdefgah0-9]".toRegex(), "")) {
            "\'" -> value++
            "," -> value--
        }

        return Token.OCTAVE(value).also(::println)
    }
}

fun main() {
    val input = """
        \relative
        {
        \clef bass
        c2 d2 e5' f, g
        }
        {a c h cs}""".trimMargin()
    val lexer = Lexer(input)
//    while(lexer.next().also(::println) !is Token.END_OF_FILE) {}
}