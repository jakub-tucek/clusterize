package cz.fit.metacentrum

import org.mockito.Mockito

/**
 *
 * @author Jakub Tucek
 */
@Suppress("UNCHECKED_CAST")
object KotlinMockito {
    fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T> eq(value: Any): T {
        Mockito.eq(value)
        return uninitialized()
    }

    fun <T> isA(java: Class<T>): T {
        Mockito.isA(java)
        return uninitialized()
    }

    fun <T> uninitialized(): T = null as T
}