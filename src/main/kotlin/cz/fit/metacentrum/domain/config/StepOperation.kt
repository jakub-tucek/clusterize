package cz.fit.metacentrum.domain.config

import java.util.function.BinaryOperator
import java.util.function.IntBinaryOperator

// Specifies enum for int step operation and provides binary operation implementation for such step and checking bound.
enum class StepOperation : BinaryOperator<Int>, IntBinaryOperator {
    PLUS {
        override fun compare(from: Int, to: Int): Boolean = from <= to

        override fun apply(left: Int, right: Int): Int = left + right
    },
    MINUS {
        override fun compare(from: Int, to: Int): Boolean = from >= to

        override fun apply(left: Int, right: Int): Int = left - right
    },
    MULTIPLY {
        override fun compare(from: Int, to: Int): Boolean = from <= to

        override fun apply(left: Int, right: Int): Int = left * right
    },
    DIVIDE {
        override fun compare(from: Int, to: Int): Boolean = from >= to

        override fun apply(left: Int, right: Int): Int = left / right
    };

    override fun applyAsInt(t: Int, u: Int) = apply(t, u)

    // compares bounds is they can be given more human friendly
    abstract fun compare(from: Int, to: Int): Boolean
}