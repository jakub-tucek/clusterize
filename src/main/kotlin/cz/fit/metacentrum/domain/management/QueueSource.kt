package cz.fit.metacentrum.domain.management

/**
 *
 * @author Jakub Tucek
 */
data class QueueSource(
        val dataSource: ClusterDetails,
        val queueTypeMapping: Map<String, QueueSourceType>
)

data class QueueSourceType(
        val clusterQueueType: ClusterQueueType,
        val queueNameMapping: Map<String, QueueInformation>
)

data class QueueInformation(
        val queue: String,
        val states: String,
        val priority: Int,
        val minWallTime: String,
        val maxWallTime: String,
        val jobStateInformation: QueueJobsInformation,
        val maxCPUsPerUser: Int?
)

data class QueueJobsInformation(
        val queuedJobs: Int,
        val runningJobs: Int,
        val completedJobs: Int,
        val totalJobs: Int,
        val maxJobsPerUser: Int?
)
