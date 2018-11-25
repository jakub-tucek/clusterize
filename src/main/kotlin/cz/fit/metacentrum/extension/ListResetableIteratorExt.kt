package cz.fit.metacentrum.extension


/**
 *
 * @author Jakub Tucek
 */
fun <T> List<T>.resetableIterator(): ResetableIterator<T> {
    open class ResetableIteratorImpl : ResetableIterator<T> {
        protected var index = 0

        override fun hasNext(): Boolean = index < size

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return get(index++)
        }

        override fun reset() {
            index = 0
        }
    }

    return ResetableIteratorImpl()
}


