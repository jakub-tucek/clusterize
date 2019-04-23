package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.extension.ResetableIterator
import cz.fit.metacentrum.extension.resetableIterator
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter

/**
 * Generates iteration combinations.
 *
 * @author Jakub Tucek
 */
class IterationExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Generating all parameters combinations")
        val iterationVariablesRanges = metadata.configFile.iterations.asSequence()
                .map {
                    val values = when (it) {
                        is ConfigIterationArray -> it.values
                        is ConfigIterationIntRange -> createIntRangeSequence(it)
                    }
                    Pair(it.name, values.resetableIterator())
                }.toList()

        val combinations = generateCombinations(iterationVariablesRanges)

        ConsoleWriter.writeStatusDetail("Total size: ${combinations.size}")
        return metadata.copy(iterationCombinations = combinations)
    }

    private fun generateCombinations(options: List<Pair<String, ResetableIterator<String>>>): List<Map<String, String>> {
        val combinations: MutableList<Map<String, String>> = mutableListOf()

        while (options.first().second.hasNext()) {
            val lastItem = options.last().second
            // if current option has more values, use next one
            if (lastItem.hasNext()) {
                // append state to combinations
                combinations.add(getCurrentIteratorValues(options))
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


    private fun createIntRangeSequence(range: ConfigIterationIntRange): List<String> {
        var current = range.from

        val values = mutableListOf<String>()

        while (range.stepOperation.compare(current, range.to)) {
            values += current.toString()
            current = range.stepOperation.applyAsInt(current, range.step)
        }

        return values
    }
}