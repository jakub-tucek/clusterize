package cz.fit.metacentrum.extension

// Resetable list iterator that extends base ListIterator API
interface ResetableIterator<T> : ListIterator<T> {
    // resets iterator state
    fun reset()

    // returns currentValue item without modifying iterator state
    fun currentValue(): T

    fun nextValue(): T
    fun previousValue(): T

    // go to last item
    fun last(): T

    fun isLast(): Boolean

    fun getCurrentIndex(): Int
}