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

    // Rest
    object R: Token()

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
        println("\n\nProcessed:\n$processedInput\n\n")

        var relevantInput = false

        for(i in processedInput) {
            if(i == "}") relevantInput = false

            if(relevantInput) {
                when (i.replace("[s0-9\',]".toRegex(), "")) {
                    "c" -> validate(i).also { Token.C.also(::println) }
                    "d" -> validate(i).also { Token.D.also(::println) }
                    "e" -> validate(i).also { Token.E.also(::println) }
                    "f" -> validate(i).also { Token.F.also(::println) }
                    "g" -> validate(i).also { Token.G.also(::println) }
                    "a" -> validate(i).also { Token.A.also(::println) }
                    "bf" -> validate(i).also { Token.BF.also(::println) }
                    "b" -> validate(i).also { Token.B.also(::println) }
                    "r" -> if (i.contains("[0-9]".toRegex())) validateRestDuration(i).also { Token.R.also(::println) }
                }
            }

            if(i == "{") relevantInput = true
        }
    }

    private fun validate(input: String) {
        if (input.contains("[0-9]".toRegex())) validateNoteDuration(input)
        if (input.contains("[\',]".toRegex())) validateOctave(input)
    }

    private fun validateNoteDuration(input: String): Token {
         return Token.NOTE_DURATION(input.replace("[cdefgahr\',]".toRegex(), "").toInt()).also(::println)
    }

    private fun validateRestDuration(input: String): Token {
        return Token.REST_DURATION(input.replace("r", "").toInt()).also(::println)
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
        c2 d2 e5' f, g r2
        }
        {a c bf cs}""".trimMargin()
    val lexer = Lexer(input)
//    while(lexer.next().also(::println) !is Token.END_OF_FILE) {}
}