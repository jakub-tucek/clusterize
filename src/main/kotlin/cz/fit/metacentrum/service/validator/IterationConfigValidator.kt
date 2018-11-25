package cz.fit.metacentrum.service.validator

import cz.fit.metacentrum.domain.ValidationResult
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.ConfigIteration
import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.service.api.ConfigValidator


class IterationConfigValidator : ConfigValidator {

    override fun validate(configFile: ConfigFile) = validateIterations(configFile.iterations)

    private fun validateIterations(iterations: List<ConfigIteration>): ValidationResult {
        // validate each iteration separately
        val iterationUnitResult = iterations
                .map { this.validateIteration(it) }
                .reduce(ValidationResult.Companion::merge)
        // count and keep iteration names that are not unique
        val iterationNameCount = iterations
                .map { it.name }
                .groupingBy { it }
                .eachCount().filter { it.value > 1 }
        // if not empty merge new error
        if (iterationNameCount.isNotEmpty()) {
            val notUniqueVarsFormatted = iterationNameCount
                    .map { entry -> "[${entry.key} x ${entry.value}]" }
                    .joinToString(", ")
            return ValidationResult.merge(iterationUnitResult, "Some iteration names are not unique: $notUniqueVarsFormatted")
        }

        return iterationUnitResult
    }

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    private fun validateIteration(iteration: ConfigIteration): ValidationResult {
        // first check common required property
        val baseResult = when (iteration.name.isBlank()) {
            false -> ValidationResult()
            true -> ValidationResult("Name of config iteration cannot be blank", false)
        }

        return when (iteration) {
            is ConfigIterationArray -> {
                if (iteration.values.isEmpty()) {
                    return ValidationResult.merge(
                            baseResult,
                            "ConfigIterationArray array value cannot be empty")
                }

                baseResult
            }
            is ConfigIterationIntRange -> {
                if (iteration.from > iteration.to) {
                    return ValidationResult.merge(baseResult,
                            "ConfigIterationIntRange has invalid range: ${iteration.from} > ${iteration.to}")
                }
                if (iteration.from < 0 || iteration.to < 0) {
                    return ValidationResult.merge(baseResult,
                            "ConfigIterationIntRange has invalid values < 0: ${iteration.from}, ${iteration.to}")
                }

                return baseResult
            }
            else -> throw IllegalStateException("Unexpected Config iteration type")
        }
    }
}