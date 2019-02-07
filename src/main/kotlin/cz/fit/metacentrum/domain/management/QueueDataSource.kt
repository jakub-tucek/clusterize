package cz.fit.metacentrum.domain.management

/**
 *
 * @author Jakub Tucek
 */
data class QueueDataSource(
        val clusterName: String,
        val sourceUrl: String,
        val parserClass: String,
        val queues: List<QueueDescription>
)