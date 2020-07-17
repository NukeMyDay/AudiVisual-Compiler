class Parser(private val tokens: Lexer) {

    init {
        var token = tokens.peek()

        do {
            when (token) {
                is Token.EXPRESSION -> println("Expression: $token")
                else -> println("else: $token")
            }

            token = tokens.peek()
        } while (token != Token.END_OF_FILE)

    }

    private fun track(token: Token) {
        println("Starting Track $token")

    }
}

fun main() {
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

    val parser = Parser(Lexer(input))

}