package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.extension.ResetableIterator
import cz.fit.metacentrum.extension.resetableIterator
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 *
 * @author Jakub Tucek
 */
class IterationExecutor : TaskExecutor {

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val iterationVariablesRanges = metadata.configFile.iterations.asSequence()
                .map {
                    val values = when (it) {
                        is ConfigIterationArray -> it.values
                        is ConfigIterationIntRange -> createIntRangeSequence(it.from, it.to)
                        else -> throw IllegalStateException("Unexpected config iteration type")
                    }
                    Pair(it.name, values.resetableIterator())
                }.toList()

        val combinations = generateCombinations(iterationVariablesRanges)

        return metadata.copy(iterationCombinations = combinations)
    }

    private fun generateCombinations(options: List<Pair<String, ResetableIterator<String>>>): List<Map<String, String>> {
        var combinations: List<Map<String, String>> = mutableListOf()

        while (options.first().second.hasNext()) {
            val lastItem = options.last().second
            // if current option has more values, use next one
            if (lastItem.hasNext()) {
                // append state to combinations
                combinations += getCurrentIteratorValues(options)
                lastItem.next()
                continue
            }

            val optionIterator = options.resetableIterator()
            optionIterator.last()

            // go back (left) for all options needed
            while (optionIterator.currentValue().second.isLast()) {
                optionIterator.currentValue().second.reset()
                optionIterator.previous()
                if (optionIterator.previousIndex() == -1) break
            }
            optionIterator.currentValue().second.next()
        }

        return combinations
    }

    private fun getCurrentIteratorValues(options: List<Pair<String, ResetableIterator<String>>>): Map<String, String> =
            options.map { Pair(it.first, it.second.currentValue()) }
                    .toMap()


    private fun createIntRangeSequence(from: Int, to: Int): List<String> {
        return (from..to).toList()
                .map { it.toString() }
    }
}