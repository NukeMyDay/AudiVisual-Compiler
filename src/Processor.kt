import java.lang.Exception

class Processor (private val parser: Parser) {
    init {
        loop@while(parser.token != Token.END_OF_FILE) {
            parser.next()
            when (parser.parseToken()) {
                is Token.EXPRESSION -> println("Expression Fuck")
                is Token.Track -> println("Track Fuck")
                is Token.END_OF_FILE -> break@loop
                else -> throw Exception("Process Error")
            }
        }
    }

    private fun processTrack() {

    }

    private fun processExpression() {

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

    Processor(Parser(Lexer(input)))
}