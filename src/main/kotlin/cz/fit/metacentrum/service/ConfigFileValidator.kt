package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.*


class ConfigFileValidator {

    fun validate(configFile: ConfigFile): ValidationResult {
        val iterationsRes = validateIterations(configFile)

        return iterationsRes
    }

    private fun validateIterations(configFile: ConfigFile): ValidationResult {
        val iterationUnitResult = configFile.iterations
                .map { this.validateIteration(it, configFile.iterations) }
                .reduce(ValidationResult.Companion::merge)
        val multipleVariables = configFile.iterations
                .map { it.name }
                .groupingBy { it }
                .eachCount().filter { it.value > 1 }
        if (multipleVariables.isNotEmpty()) {
            ValidationResult.merge(iterationUnitResult, "Some iteration names are not unique: $multipleVariables")
        }

        return iterationUnitResult
    }

    private fun validateIteration(iteration: ConfigIteration, iterations: List<ConfigIteration>): ValidationResult {
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
            is ConfigIterationDependent -> {
                if (iterations.asSequence().find { it.name == iteration.dependentVarName } == null) {
                    return ValidationResult.merge(
                            baseResult,
                            "ConfigIterationDependent variable does not exist in other iterations"
                    )
                }
                return baseResult
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
//            else -> ValidationResult.merge(baseResult, "Unknown config iteration type: ${iteration.javaClass.name} ")
        }
    }
}