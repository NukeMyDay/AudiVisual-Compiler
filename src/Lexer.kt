sealed class Token {
    override fun toString(): String = javaClass.simpleName
    // Notes
    object C: Token()
    object D: Token()
    object E: Token()
    object F: Token()
    object G: Token()
    object A: Token()
    object B: Token()
    object H: Token()

    // Abstracts
    object LEFT_CURLY_PAREN: Token()
    object RIGHT_CURLY_PAREN: Token()

    // End of File
    object END_OF_FILE: Token()
}

class Peekable<T>(val iterator: Iterator<T>) {
    var lookahead: T? = null
    fun next(): T? = when {
        lookahead != null -> lookahead.also { lookahead = null }
        iterator.hasNext() -> iterator.next()
        else -> null
    }
}

class Lexer(input: String) {
    override fun toString(): String = javaClass.simpleName
    private val chars = Peekable(input.iterator())
    private var lookahead: Token? = null

    // fun peek(): Token? {
    //     lookahead = next()
    //     return lookahead
    // }

    fun next(): Token {
        val char = chars.next() ?: return Token.END_OF_FILE
        return when(char) {
            'c' -> Token.C
            'd' -> Token.D
            'e' -> Token.E
            'f' -> Token.F
            'g' -> Token.G
            'a' -> Token.A
            'b' -> Token.B
            'h' -> Token.H
            '{' -> Token.LEFT_CURLY_PAREN
            '}' -> Token.RIGHT_CURLY_PAREN
            else -> when {
                char.isWhitespace() -> next()
                else -> throw Exception("Unexpected $char")
            }
        }
    }
}

fun main() {
    val input = "{c d e f g}{a c h}"
    val lexer = Lexer(input)
    while(lexer.next().also(::println) !is Token.END_OF_FILE) {}
}
