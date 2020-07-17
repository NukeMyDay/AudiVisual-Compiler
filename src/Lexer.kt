import java.lang.Exception

sealed class Token {
    // Keywords
    data class NOTE(val type: String, val duration: String?, val octave: String?): Token()
    data class EXPRESSION(val type: String, val value: String?): Token()
    data class REST(val duration: String?): Token()

    // Symbols
    object LEFT_PAREN: Token()
    object RIGHT_PAREN: Token()
    object LEFT_TAG: Token()
    data class RIGHT_TAG(val duration: String?): Token()

    // End of File
    object END_OF_FILE: Token()

    override fun toString(): String = javaClass.simpleName
}

class Peekable<T>(val iterator: Iterator<T>) {
    var lookahead: T? = null
    fun next(): T? = when {
        lookahead != null -> lookahead.also { lookahead = null }
        iterator.hasNext() -> iterator.next()
        else -> null
    }

    fun peek(): T? = next().also { lookahead = it }
}

class Lexer(notation: String) {
    private val input = Peekable(notation.iterator())
    private var lookahead: Token? = null

    override fun toString(): String = javaClass.simpleName

    fun peek(): Token = next().also { lookahead = it }

    fun next(): Token {
        val char = input.next() ?: return Token.END_OF_FILE

        return when (char) {
            '{' -> Token.LEFT_PAREN
            '}' -> Token.RIGHT_PAREN
            '<' -> Token.LEFT_TAG
            '>' -> Token.RIGHT_TAG(if(input.peek()?.isDigit() == true) input.next().toString() else null)
            'r' -> Token.REST(if(input.peek()?.isDigit() == true) input.next().toString() else null)
            '\\' -> expression()
            'c', 'd', 'e', 'f', 'g', 'a', 'b' -> note(char)
            else ->  when {
                char.isWhitespace() -> next()
                else -> next()
            }
        }
    }

    private fun note(char: Char): Token {
        var res = char.toString()
        while (input.peek()?.isWhitespace() == false && input.peek() != '>') res += input.next()

        val duration: String? = if (res.replace("[a-z\',]".toRegex(), "").isNotEmpty()) res.replace("[a-z\',]".toRegex(), "") else null
        val octave: String? = if (res.replace("[a-z0-9.]".toRegex(), "").isNotEmpty()) res.replace("[a-z0-9.]".toRegex(), "") else null

        res = res.replace("[0-9\',.]".toRegex(), "")

        return when {
            else -> when (res) {
                "c",
                "d",
                "e",
                "f",
                "g",
                "a",
                "b",
                "bes",
                "fes" -> Token.NOTE(res, duration, octave)
                else -> next()
            }
        }
    }

    private fun expression(): Token {
        var type = ""
        while (input.peek()?.isWhitespace() == false) type += input.next()

        // Skip the whitespace
        input.next()

        return when (type) {

            // Expression without value
            "break",
            "stemDown",
            "stemUp",
            "fermata",
            "mark",
            "markup" -> Token.EXPRESSION(type, null)

            // Expression with value
            else -> {
                var value = ""
                while (!input.peek()?.isWhitespace()!!) value += input.next()
                Token.EXPRESSION(type, value)
            }
        }
    }
}

fun main () {
    val input = """
    \relative c'
    {
        \clef "treble" \numericTimeSignature\time 4/4 \tempo 4=40
        c2 d e <f g>2 g a4 a fes a a g1 a4 a a a \break g1 f4 f f f e2 e d4 d d d c1
    }
    {
        \clef "treble" \numericTimeSignature\time 4/4 \tempo 4=40
        c2 d e <f g>2 g a4 a fes a a g1 a4 a a a \break g1 f4 f f f e2 e d4 d d d c1
    }
    """.trimMargin()

    val lexer = Lexer(input)
    while(lexer.next().also(::println) !is Token.END_OF_FILE) {continue}
}