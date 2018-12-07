package cz.fit.metacentrum.domain

data class ValidationResult(val messages: List<String> = emptyList(),
                            val success: Boolean = true) {


    constructor(message: String, success: Boolean) : this(listOf(message), success) {
    }

    companion object {
        fun merge(a1: ValidationResult, a2: ValidationResult): ValidationResult {
            val newList = ArrayList<String>()
            newList.addAll(a1.messages)
            newList.addAll(a2.messages)
            return ValidationResult(newList, a1.success.and(a2.success))
        }

        fun merge(a1: ValidationResult, newMessages: String): ValidationResult {
            val newList = ArrayList<String>()
            newList.addAll(a1.messages)
            newList.add(newMessages)
            return ValidationResult(newList, false)
        }
    }

    operator fun plus(newRes: ValidationResult): ValidationResult {
        return merge(this, newRes)
    }

    operator fun plus(newMessages: String): ValidationResult {
        return merge(this, newMessages)
    }
}
