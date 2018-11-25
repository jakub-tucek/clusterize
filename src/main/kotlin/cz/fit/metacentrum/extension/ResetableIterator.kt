package cz.fit.metacentrum.extension

interface ResetableIterator<T> : Iterator<T> {
    // resets iterator state
    fun reset()
}