package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.extension.resetableIterator
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 *
 * @author Jakub Tucek
 */
class IterationExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val options = metadata.configFile.iterations.asSequence()
                .map {
                    val values = when (it) {
                        is ConfigIterationArray -> it.values
                        is ConfigIterationIntRange -> createIntRangeSequence(it.from, it.to)
                        else -> throw IllegalStateException("Unexpected config iteration type")
                    }
                    Pair(it.name, values.resetableIterator())
                }



        return metadata
    }

    private fun createIntRangeSequence(from: Int, to: Int): List<String> {
        return (from..to).toList()
                .map { it.toString() }
    }
}