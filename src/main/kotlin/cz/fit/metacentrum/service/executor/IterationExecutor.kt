package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 *
 * @author Jakub Tucek
 */
class IterationExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        metadata.configFile.iterations.asSequence()
                .map { configIteration ->
                    val possibleValues: List<String> = when (configIteration) {
                        is ConfigIterationArray -> configIteration.values
                        is ConfigIterationIntRange -> createIntRangeSequence(configIteration.from, configIteration.to)
                        else -> throw IllegalStateException("Unexpected config iteration type")
                    }
                }

        return metadata
    }

    private fun createIntRangeSequence(from: Int, to: Int): List<String> {
        return (from..to).toList()
                .map { it.toString() }
    }
}