package cz.fit.metacentrum.service.input.validator

import cz.fit.metacentrum.domain.ValidationResult
import cz.fit.metacentrum.domain.config.*
import cz.fit.metacentrum.service.api.ConfigValidator


class IterationConfigValidator : ConfigValidator {

    override fun validate(configFile: ConfigFile) = validateIterations(configFile.iterations)

    private fun validateIterations(iterations: List<ConfigIteration>): ValidationResult {
        // validate each iteration separately
        var iterationUnitResult = iterations
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
                    .map { entry -> "[${entry.key} found ${entry.value} times]" }
                    .joinToString(", ")
            iterationUnitResult += "Some iteration names are not unique: $notUniqueVarsFormatted"
        }

        return iterationUnitResult
    }

    private fun validateIteration(iteration: ConfigIteration): ValidationResult {
        // first check common required property
        var baseResult = when (iteration.name.isBlank()) {
            false -> ValidationResult()
            true -> ValidationResult("Name of config iteration cannot be blank", false)
        }

        when (iteration) {
            is ConfigIterationArray -> {
                if (iteration.values.isEmpty()) {
                    baseResult += "ConfigIterationArray array value cannot be empty"
                }
            }
            is ConfigIterationIntRange -> {
                if (!iteration.stepOperation.compare(iteration.from, iteration.to)) {
                    baseResult += "ConfigIterationIntRange has invalid range that provides no values: ${iteration.from}, ${iteration.to}"
                }
                if (iteration.from < 0 || iteration.to < 0) {
                    baseResult += "ConfigIterationIntRange has invalid values < 0: ${iteration.from}, ${iteration.to}"
                }
                if (iteration.step == 0) {
                    baseResult += "Iteration step cannot be 0"
                }
                if (iteration.step == 1 && (iteration.stepOperation == StepOperation.DIVIDE || iteration.stepOperation == StepOperation.MULTIPLY)) {
                    baseResult += "Iteration step cannot be 1 if DIVISION of MULTIPLICATION is given"
                }
            }
        }
        return baseResult
    }
}