import java.awt.geom.Arc2D

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
    data class NOTE_DURATION(val value: Double): Token()
    data class REST_DURATION(val value: Int): Token()

    // Octave
    data class OCTAVE(val value: Int): Token()

    // Abstracts
    object LEFT_PAREN: Token()
    object RIGHT_PAREN: Token()
    object LEFT_TAG: Token()
    object RIGHT_TAG: Token()

    // End of File
    object END_OF_FILE: Token()

    override fun toString(): String = javaClass.simpleName
}

class Lexer(input: String) {
    private var myInput = input.split("\n")
    private val preprocessed = mutableListOf<String>()
    private var preStringed = ""
    private val validatedInput = mutableListOf<String>()
    private val processedInput = mutableListOf<String>()

    init {
        println("Input:\n$input")

        // We need to add preprocessed elements of myinput to validatedInput
        for(i in myInput) {
            if(i.contains("%")) continue
            i.trim().split(" ").also{ if(i != "") preprocessed.add(i) }
        }

        for(i in preprocessed) {
            if(i.first() == '%') preprocessed.remove(i)
        }

        for(i in preprocessed) preStringed += i

        val preList = preStringed.split(" ")

        for(i in preList) {
            i.trim().also{ if(i != "") validatedInput.add(i) }
        }

        println("\n\npreList:\n$preList\n\n")


        println("validatedInput:\n$validatedInput")

        // We need to split abstracts and maybe bonded keywords or notes
        for(i in validatedInput) {

            when {
                i.contains("<") -> processedInput.add("<").also { processedInput.add(i.replace("<", "")) }
                i.contains(">") -> processedInput.add(i.replace(">", "")).also { processedInput.add(">") }
                i.first() == '\\' -> processedInput.add("\\").also { processedInput.add(i.replace("\\", "")) }
                i.first() == '{' -> when(i.length) {
                        1 -> processedInput.add(i)
                        else -> processedInput.add("{").also { processedInput.add(i.replace("{", "")) }
                    }
                i.last() == '{' -> when(i.length) {
                        1 -> processedInput.add(i)
                        else -> processedInput.add(i.replace("{", "")).also { processedInput.add("{") }
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
                when (i.first()) {
                    '<' -> Token.LEFT_TAG.also(::println)
                    '>' -> Token.RIGHT_TAG.also(::println)
                    'c' -> validate(i).also { Token.C.also(::println) }
                    'd' -> validate(i).also { Token.D.also(::println) }
                    'e' -> validate(i).also { Token.E.also(::println) }
                    'f' -> validate(i).also { Token.F.also(::println) }
                    'g' -> validate(i).also { Token.G.also(::println) }
                    'a' -> validate(i).also { Token.A.also(::println) }
//                    'bf' -> validate(i).also { Token.BF.also(::println) }
                    'b' -> validate(i).also { Token.B.also(::println) }
                    'r' -> if (i.contains("[0-9]".toRegex())) validateRestDuration(i).also { Token.R.also(::println) }
                }
            }

            if(i == "{") relevantInput = true
        }
    }

    private fun validate(input: String) {
        if (input.contains("[0-9.]".toRegex())) validateNoteDuration(input.replace("[es\',]".toRegex(), ""))
        if (input.contains("[\',]".toRegex())) validateOctave(input.replace("[es0-9.]".toRegex(), ""))
        if (input.contains("[es]".toRegex())) validateIntermediateTone(input.replace("[0-9.\',]".toRegex(), ""))
    }

    private fun validateIntermediateTone(input: String) {
        when {
            input.replaceFirst("[cdefgab]".toRegex(), "").contains("es") -> println("es")
            input.contains("s") -> println("s")
        }
    }

    private fun validateNoteDuration(input: String): Token {
        var value = 1.0
        when {
            input.contains("[0-9]".toRegex()) -> value /= input.replace("[a-z.]".toRegex(), "").toInt()
            input.contains(".") -> value *= 1.5
        }
        return Token.NOTE_DURATION(value).also(::println)
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
    PartPOneVoiceOne =  \relative c' {
    \clef "treble" \key as \major \numericTimeSignature\time 4/4 | % 1
    \tempo 4=40
    \stemUp c2 _\mp \stemUp des2 | % 2
    \stemUp c2 \stemUp bes2 | % 3
    \stemUp c2 \stemUp des2 | % 4
    \stemUp <c es>2 \stemUp <bes es>2 ^\fermata \bar "||"
    \break | % 5
    \time 12/8  | % 5
    \tempo 4.=78
    \stemDown f'2. _\p \stemDown f2. | % 6
    \stemDown f2. \stemDown f2. \break | % 7
    \stemDown es2. \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ | % 8
    \stemDown es4. ~ \stemDown es4 \stemDown es8 \stemDown es4. ~
    \stemDown es4 \stemDown es8 ~ | % 9
    es1. \break | \barNumberCheck #10
    \stemUp g8 \stemUp f4 \stemUp f4 \stemUp f8 \stemUp g8 \stemUp f4
    \stemUp f4 \stemUp f8 | % 11
    \stemDown es2. ~ \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ | % 12
    \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ \stemDown es4. ~
    \stemDown es4 \stemDown es8 ~ \bar "||"
    \break | % 13
    \tempo 4.=80
    es1. | % 14
    \stemDown bes'8 \stemUp as4 \stemUp as4 \stemUp as8 \stemDown bes8
    \stemUp as4 \stemUp as4 \stemUp as8 \pageBreak | % 15
    a,8 \rest \stemDown es'4 ~ \stemDown es4. a,8 \rest \stemDown es'4 ~
    \stemDown es4 \stemDown es8 ~ | % 16
    \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ \stemDown es4. ~
    \stemDown es4 \stemDown es8 ~ \break | % 17
    es1. | % 18
    \stemUp g8 \stemUp f4 \stemUp f4 \stemUp f8 \stemUp g8 \stemUp f4
    \stemUp f4 \stemUp f8 \break | % 19
    \stemDown es2. \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ |
    \barNumberCheck #20
    \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ \stemDown es4. ~
    \stemDown es4 \stemDown es8 ~ \bar "||"
    \break | % 21
    \tempo 4.=84 | % 21
    \stemDown es2. _\mp a'4 \rest c2 \rest | % 22
    \stemUp as,2. ~ \stemUp as8 [ \stemUp es8 \stemUp bes'8 ] \stemDown
    c8 [ \stemDown bes8 \stemDown as8 ~ ] \break | % 23
    \stemDown as4. \stemDown es4. ~ \stemUp <es c'>8 [ \stemUp es8
    \stemUp bes'8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ~ ] | % 24
    \stemDown as4. \stemDown es4. ~ \stemUp <es bes'>8 [ \stemUp es8
    \stemUp bes'8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ] | % 25
    \stemUp bes8 [ \stemUp as8 \stemUp es8 ~ ] \stemUp es4. ~ \stemUp es8
    [ \stemUp es8 \stemUp bes'8 ] \stemUp c8 [ \stemUp bes8 \stemUp as8
    ~ ] \break | % 26
    es1. ~ | % 27
    es1. ~ | % 28
    es1. \bar "||"
    \pageBreak | % 29
    \stemDown f'2. ~ \stemDown f4. \stemDown es16 [ \stemDown des16
    \stemDown c8 \stemDown des8 ] | \barNumberCheck #30
    \stemDown es4. \stemDown c4 \stemDown bes8 ~ \stemDown bes4. \stemUp
    as4 \stemUp as8 ~ | % 31
    \stemUp as2. ~ \stemUp as4. \stemDown es'16 [ \stemDown des16
    \stemDown c8 \stemDown des8 ] \break | % 32
    \stemDown es4. \stemDown c4 \stemDown c8 \stemDown des8 [ \stemDown
    c8 \stemDown bes8 ~ ] \stemDown bes8 \stemUp as4 | % 33
    \stemUp as2. ~ \stemUp as4. ^\fermata \stemUp bes16 [ \stemUp as16
    \stemUp g8 \stemUp as8 ] | % 34
    \stemDown bes2. ~ \stemDown bes4 ^\markup{ \bold {rit.} } \stemDown
    bes8 \stemDown c8 [ ^\markup{ \bold {a tempo} } \stemDown bes8
    \stemDown as8 ] \bar "||"
    \break | % 35
    \stemUp f4 _\markup{ \small\italic {cresc.} } ^\markup{ \bold {poco
            a poco accel.} } \stemDown bes8 \stemDown c8 [ \stemDown bes8
    \stemDown as8 ] \stemUp f8 [ \stemUp as8 \stemUp bes8 ] \stemDown c8
    [ \stemDown bes8 \stemDown as8 ] | % 36
    \stemUp f2. ~ \stemUp f4 \stemDown bes8 \stemDown c8 [ \stemDown bes8
    \stemDown as8 ] | % 37
    \stemDown c8 [ \stemDown as8 \stemDown bes8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown c8 [ \stemDown as8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ~ ]
    \break | % 38
    \stemDown as2. \stemDown as4. c'8 \rest f,,,4 \rest | % 39
    \stemDown es''8 [ \stemDown as,8 \stemDown bes8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ] |
    \barNumberCheck #40
    \stemDown es'8 [ \stemDown as,8 \stemDown bes8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ]
    \break | % 41
    \stemDown es'8 [ \stemDown as,8 \stemDown bes8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ] | % 42
    \stemDown es'8 [ \stemDown as,8 \stemDown bes8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown es8 [ \stemDown as,8 \stemDown bes8 ]
    \break | % 43
    \tempo 4.=94 | % 43
    \stemUp as8 [ _\mf \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp c'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] | % 44
    \stemUp as8 [ \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp des'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] | % 45
    \stemDown c'8 [ \stemDown bes8 \stemDown as8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ]
    \pageBreak | % 46
    \stemUp bes8 [ \stemUp as8 \stemUp g8 ] \stemUp bes8 [ \stemUp as8
    \stemUp g8 ] \stemDown es'8 [ \stemDown g,8 \stemDown as8 ] \stemUp
    bes8 [ \stemUp as8 \stemUp g8 ] | % 47
    \stemUp as8 [ \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp c'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] | % 48
    \stemUp as8 [ \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp des'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] \break | % 49
    \stemDown c'8 [ \stemDown bes8 \stemDown as8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ] |
    \barNumberCheck #50
    \stemUp bes8 [ \stemUp as8 \stemUp g8 ] \stemUp bes8 [ \stemUp as8
    \stemUp g8 ] \stemDown es'8 [ \stemDown g,8 \stemDown as8 ] \stemUp
    bes8 [ \stemUp as8 \stemUp g8 ] \break | % 51
    \tempo 4.=96 | % 51
    \stemDown as'8 [ _\f \stemDown as,8 \stemDown c8 ] \stemDown g'8 [
    \stemDown as,8 \stemDown c8 ] \stemDown f8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ] | % 52
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ \stemDown g,8
    \stemDown bes8 ] \stemDown es8 [ \stemDown g,8 \stemDown bes8 ] | % 53
    \stemDown as'8 [ \stemDown as,8 \stemDown g'8 ] \stemDown as,8 [
    \stemDown f'8 \stemDown as,8 ] \stemDown f'8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ]
    \break | % 54
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ \stemDown g,8
    \stemDown bes8 ] \stemDown es8 [ \stemDown g,8 \stemDown bes8 ] | % 55
    \stemDown as'8 [ \stemDown as,8 \stemDown c8 ] \stemDown g'8 [
    \stemDown as,8 \stemDown c8 ] \stemDown f8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ] | % 56
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ \stemDown g,8
    \stemDown bes8 ] \stemDown es8 [ \stemDown g,8 \stemDown bes8 ]
    \break | % 57
    \stemDown as'8 [ \stemDown as,8 \stemDown g'8 ] \stemDown as,8 [
    \stemDown f'8 \stemDown as,8 ] \stemDown f'8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ] | % 58
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ ^\markup{ \bold {molto
            rit.} } _\> \stemDown g,8 \stemDown bes8 ] \stemDown es8 [
    \stemDown g,8 \stemDown bes8 ~ ] _\p | % 59
    bes1. ^\fermata _\! \bar "||"
    \break | \barNumberCheck #60
    \numericTimeSignature\time 4/4  | \barNumberCheck #60
    \tempo 4=40
    \stemUp c,2 _\mp \stemUp des2 | % 61
    \stemUp c2 \stemUp bes2 | % 62
    \stemUp c2 \stemUp des2 | % 63
    \stemUp <c es>2 \stemUp <bes es>2 ^\fermata \bar "||"
    \pageBreak | % 64
    \time 12/8  | % 64
    \tempo 4.=78 | % 64
    f'1. _\p | % 65
    f1. \break | % 66
    \stemDown es2. ~ \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ | % 67
    \stemDown es2. ~ \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ | % 68
    es1. \break | % 69
    \stemUp g8 \stemUp f4 \stemUp f4 \stemUp f8 \stemUp g8 \stemUp f4
    \stemUp f4 \stemUp f8 | \barNumberCheck #70
    \stemDown es2. ~ \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ | % 71
    \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ \stemDown es4. ~
    \stemDown es4 \stemDown es8 ~ \bar "||"
    \break | % 72
    \tempo 4.=80
    es1. | % 73
    \stemDown bes'8 \stemUp as4 \stemUp as4 \stemUp as8 \stemDown bes8
    \stemUp as4 \stemUp as4 \stemUp as8 \break | % 74
    \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ \stemDown es4. ~
    \stemDown es4 \stemDown es8 ~ | % 75
    \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ \stemDown es4. ~
    \stemDown es4 \stemDown es8 ~ \break | % 76
    es1. | % 77
    \stemUp g8 \stemUp f4 \stemUp f4 \stemUp f8 \stemUp g8 \stemUp f4
    \stemUp f4 \stemUp f8 \pageBreak | % 78
    \stemDown es4. \stemDown es4. \stemDown es4. ~ \stemDown es4
    \stemDown es8 ~ | % 79
    \stemDown es4. ~ \stemDown es4 \stemDown es8 ~ \stemDown es4. ~
    \stemDown es4 \stemDown es8 \bar "||"
    \break | \barNumberCheck #80
    \tempo 4.=90 | \barNumberCheck #80
    \stemUp as8 [ _\mf \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp c'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] | % 81
    \stemUp as8 [ \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp des'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] | % 82
    \stemDown c'8 [ \stemDown bes8 \stemDown as8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ]
    \break | % 83
    \stemUp bes8 [ \stemUp as8 \stemUp g8 ] \stemUp bes8 [ \stemUp as8
    \stemUp g8 ] \stemDown es'8 [ \stemDown g,8 \stemDown as8 ] \stemUp
    bes8 [ \stemUp as8 \stemUp g8 ] | % 84
    \stemUp as8 [ \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp c'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] | % 85
    \stemUp as8 [ \stemUp g8 \stemUp f8 ] \stemUp as8 [ \stemUp g8
    \stemUp f8 ] \stemUp des'8 [ \stemUp f,8 \stemUp g8 ] \stemUp as8 [
    \stemUp g8 \stemUp f8 ] \break | % 86
    \stemDown c'8 [ \stemDown bes8 \stemDown as8 ] \stemDown c8 [
    \stemDown bes8 \stemDown as8 ] \stemDown es'8 [ \stemDown as,8
    \stemDown bes8 ] \stemDown c8 [ \stemDown bes8 \stemDown as8 ] | % 87
    \stemUp bes8 [ \stemUp as8 \stemUp g8 ] \stemUp bes8 [ \stemUp as8
    \stemUp g8 ] \stemDown es'8 [ \stemDown g,8 \stemDown as8 ] \stemUp
    bes8 [ \stemUp as8 \stemUp g8 ] \break | % 88
    \tempo 4.=96 | % 88
    \stemDown as'8 [ _\f \stemDown as,8 \stemDown c8 ] \stemDown g'8 [
    \stemDown as,8 \stemDown c8 ] \stemDown f8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ] | % 89
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ \stemDown g,8
    \stemDown bes8 ] \stemDown es8 [ \stemDown g,8 \stemDown bes8 ]
    \break | \barNumberCheck #90
    \stemDown as'8 [ \stemDown as,8 \stemDown g'8 ] \stemDown as,8 [
    \stemDown f'8 \stemDown as,8 ] \stemDown f'8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ] | % 91
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ \stemDown g,8
    \stemDown bes8 ] \stemDown es8 [ \stemDown g,8 \stemDown bes8 ]
    \pageBreak | % 92
    \stemDown as'8 [ \stemDown as,8 \stemDown c8 ] \stemDown g'8 [
    \stemDown as,8 \stemDown c8 ] \stemDown f8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ] | % 93
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ \stemDown g,8
    \stemDown bes8 ] \stemDown es8 [ \stemDown g,8 \stemDown bes8 ]
    \break | % 94
    \stemDown as'8 [ \stemDown as,8 \stemDown g'8 ] \stemDown as,8 [
    \stemDown f'8 \stemDown as,8 ] \stemDown f'8 [ \stemDown as,8
    \stemDown des8 ] \stemDown es8 [ \stemDown as,8 \stemDown des8 ] | % 95
    \stemDown es8 [ \stemDown as,8 \stemDown c8 ] \stemDown es8 [
    \stemDown as,8 \stemDown c8 ] \stemDown es8 [ \stemDown g,8
    \stemDown bes8 ] \stemUp es8 ^\markup{ \bold {molto rit.} } \stemUp
    g,4 ~ | % 96
    g1. ^\fermata \bar "||"
    \break | % 97
    \tempo 4.=88
    \stemUp bes8 [ _\p ^\markup{ \small\italic {ten.} } \stemUp es,8
    \stemUp f8 ] \stemUp as8 [ \stemUp es8 \stemUp f8 ] \stemUp bes8 [
    \stemUp es,8 \stemUp f8 ] \stemUp as8 [ \stemUp es8 \stemUp f8 ] | % 98
    \stemUp bes8 [ \stemUp es,8 \stemUp f8 ] \stemUp as8 [ \stemUp es8
    \stemUp f8 ] \stemUp bes8 [ \stemUp es,8 \stemUp f8 ] \stemUp c'8 [
    \stemUp es,8 ^\markup{ \small\italic {ten.} } \stemUp f8 ] | % 99
    \stemUp bes8 [ ^\markup{ \small\italic {ten.} } \stemUp es,8 \stemUp
    f8 ] \stemUp as8 [ \stemUp es8 \stemUp f8 ] \stemUp bes8 [ \stemUp
    es,8 \stemUp f8 ] \stemUp c'8 [ \stemUp es,8 \stemUp f8 ] \break |
    \barNumberCheck #100
    \stemUp bes8 [ \stemUp es,8 \stemUp f8 ] \stemUp as8 [ \stemUp es8
    \stemUp f8 ] \stemUp bes8 [ ^\markup{ \bold {rit.} } \stemUp es,8
    \stemUp f8 ] \stemUp as4. ^\fermata | % 101
    \stemUp bes8 [ ^\markup{ \bold {a tempo} } ^\markup{ \small\italic
        {ten.} } \stemUp es,8 \stemUp f8 ] \stemUp as8 [ \stemUp es8
    \stemUp f8 ] \stemUp bes8 [ \stemUp es,8 \stemUp f8 ] \stemUp c'8 [
    \stemUp es,8 ^\markup{ \small\italic {ten.} } \stemUp f8 ] | % 102
    \stemUp bes8 [ \stemUp es,8 \stemUp f8 ] \stemUp as8 [ \stemUp es8
    \stemUp f8 ] \stemUp bes8 [ ^\markup{ \bold {molto rit.} } \stemUp
    es,8 \stemUp f8 ^\fermata ] \stemUp as4. ^\fermata \bar "|."
    }
        """.trimMargin()
    val lexer = Lexer(input)
//    while(lexer.next().also(::println) !is Token.END_OF_FILE) {}
}