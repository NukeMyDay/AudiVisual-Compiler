class Parser(private val tokens: Lexer) {
    var token: Token? = null

    fun next() {
        token = tokens.peek()
    }

    fun parseToken(): Token? = when (token) {
        is Token.LEFT_PAREN -> track()
        is Token.EXPRESSION -> token
        is Token.END_OF_FILE -> Token.END_OF_FILE
        else -> null
    }

    private fun track(): Token.Track {
        val track = mutableListOf<Token?>()

        token = tokens.peek()

        while(token != Token.RIGHT_PAREN) {
            when(token) {
                is Token.LEFT_TAG -> track += chord()
                else -> {
                    track += token
                    token = tokens.peek()
                }
            }
        }
        return Token.Track(track)
    }

    private fun chord(): Token.Chord {
        val chord = mutableListOf<Token?>()

        token = tokens.peek()

        while(token is Token.NOTE) {
            chord += token
            token = tokens.peek()
        }

        token = tokens.peek()
        return Token.Chord(chord)
    }
}

fun main() {
    val input = """
    {
        \clef "treble" \numericTimeSignature\time 4/4 \tempo 4=40
        c2 d e <f g>2 g a4 a fes a a g1 a4 a a a g1 f4 f f f e2 e d4 d d d c1
    }
    """.trimMargin()

    val parser = Parser(Lexer(input))
    parser.next()
    while(parser.token !is Token.END_OF_FILE) {
        parser.parseToken().also(::println)
        parser.next()
    }
}