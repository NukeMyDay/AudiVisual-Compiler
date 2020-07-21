class Interpreter(private val processed: Processor) {
    private fun xAxis() {
        processed.processNext()
    }

    private fun yAxis() {

    }
}

fun main() {
    val input = """
    {
        \clef "treble" \numericTimeSignature\time 4/4 \tempo 4=40
        c2 d e f g g a4 a fes a a g1 a4 a a a g1 f4 f f f e2 e d4 d d d c1
    }
    """.trimMargin()

    val interpreter = Interpreter(Processor(Parser(Lexer(input))))
}