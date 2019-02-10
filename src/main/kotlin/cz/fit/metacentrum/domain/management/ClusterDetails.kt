package cz.fit.metacentrum.domain.management

/**
 *
 * @author Jakub Tucek
 */
data class ClusterDetails(
        val clusterName: String,
        val queueTypes: List<ClusterQueueType>
)