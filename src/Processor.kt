import java.lang.Exception

sealed class Type {
    data class SOUNDMAP(val notes: MutableList<Type>): Type()
    data class NOTE(val position: Double, val duration: Int): Type()
    override fun toString(): String = javaClass.simpleName
}

class Processor (private val parser: Parser) {
    var clef: Int = 0
    var measure: Double = 0.0
    var tempo: Int = 0
    var noteDuration: Int = 0

    fun processNext() {
        loop@while(parser.token != Token.END_OF_FILE) {
            parser.next()
            val parsedToken = parser.parseToken()
            when (parsedToken) {
                is Token.EXPRESSION -> processExpression(parsedToken)
                is Token.Track -> processTrack(parsedToken.objects)
                is Token.END_OF_FILE -> break@loop
                else -> throw Exception("Process Error")
            }.also(::println)
        }
    }

    private fun processTrack(tokens: MutableList<Token?>) {
        val track = mutableListOf<Type>()

        loop@for (i in tokens) {
            when(i) {
                is Token.NOTE -> track += processNote(i)
//                is Token.Chord -> TODO()
                is Token.EXPRESSION -> processExpression(i)
                else -> continue@loop
            }
        }

        Type.SOUNDMAP(track).also(::println)
    }

    private fun processExpression(expression: Token.EXPRESSION) {
        when (expression.type) {
            "clef" -> clef = when(expression.value) {
                "treble" -> 0
                "bass" -> -2
                else -> 0
            }
            "numericTimeSignature\\time" -> measure = when(expression.value) {
                "4/4" -> 0.25
                else -> 0.0
            }
            "tempo" -> tempo = when(expression.value) {
                "4=40" -> 40
                else -> 0
            }
            "break" -> TODO()
            else -> TODO()
        }
    }

    private fun processNote(note: Token.NOTE): Type.NOTE {
        var notePosition: Double = when(note.type) {
            "c" -> -2.0
            "d" -> -1.0
            "e" -> 0.0
            "fes" -> 0.5
            "f" -> 1.0
            "g" -> 2.0
            "a" -> 3.0
            "b" -> 4.0
            else -> throw Exception("Unexpected Notation: ${note.type}")
        }

        notePosition += when(note.octave) {
            "'" -> 8
            "''" -> 16
            "'''" -> 24
            "," -> -8
            ",," -> -16
            ",,," -> -24
            else -> 0
        }

        if(clef != 0) notePosition += clef

        var duration = (60000 / tempo)
        if(note.duration != null) noteDuration = note.duration.toInt()

        if(noteDuration != 0) duration /= noteDuration
        else duration *= measure.toInt()

        return Type.NOTE(notePosition, duration)
    }

    private fun processSoundMap() {

    }
}

fun main() {
    val input = """
    {
        \clef "treble" \numericTimeSignature\time 4/4 \tempo 4=40
        c2 d e f g g a4 a fes a a g1 a4 a a a g1 f4 f f f e2 e d4 d d d c1
    }
    """.trimMargin()

    Processor(Parser(Lexer(input)))
}