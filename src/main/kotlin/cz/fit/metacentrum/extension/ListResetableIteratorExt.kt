package cz.fit.metacentrum.extension


/**
 *
 * @author Jakub Tucek
 */
fun <T> List<T>.resetableIterator(): ResetableIterator<T> {
    open class ResetableIteratorImpl : ResetableIterator<T> {
        protected var index = 0

        override fun getCurrentIndex(): Int = index

        override fun reset() {
            index = 0
        }

        override fun currentValue(): T {
            return get(index)
        }

        override fun nextValue(): T {
            if (!hasNext()) throw NoSuchElementException()
            return get(index + 1)
        }

        override fun previousValue(): T {
            if (!hasPrevious()) throw NoSuchElementException()
            return get(index - 1)
        }

        override fun last(): T {
            index = size - 1
            return currentValue()
        }

        override fun hasNext(): Boolean = index < size

        override fun hasPrevious(): Boolean = index > 0

        override fun next(): T {
            val nextVal = nextValue()
            index++
            return nextVal
        }

        override fun nextIndex(): Int {
            return index + 1
        }

        override fun previous(): T {
            val prevVal = previousValue()
            index--
            return prevVal
        }

        override fun previousIndex(): Int {
            return index - 1
        }
    }

    return ResetableIteratorImpl()
}


