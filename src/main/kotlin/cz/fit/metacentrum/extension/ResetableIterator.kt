package cz.fit.metacentrum.extension

interface ResetableIterator<T> : ListIterator<T> {
    // resets iterator state
    fun reset()

    // returns currentValue item without modifying iterator state
    fun currentValue(): T

    fun nextValue(): T
    fun previousValue(): T

    // go to last item
    fun last(): T

    fun getCurrentIndex(): Int
}