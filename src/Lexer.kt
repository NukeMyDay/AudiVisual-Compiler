sealed class Token {
    //Hallo
    // Notes
    object C: Token()
    object CIS: Token()
    object D: Token()
    object DIS: Token()
    object E: Token()
    object F: Token()
    object FIS: Token()
    object G: Token()
    object GIS: Token()
    object A: Token()
    object H: Token()

    // Octave Changes
    // object OCTAVE_UP: Token()
    // object OCTAVE_DOWN: Token()

    // Abstracts
    // object RELATIVE: Token()
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


class Lexer {
    /*
    TODO
     */
}