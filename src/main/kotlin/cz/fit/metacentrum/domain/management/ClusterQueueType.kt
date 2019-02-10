package cz.fit.metacentrum.domain.management


data class ClusterQueueType(
        val type: String,
        val queues: List<CluterQueue>,
        val details: Map<String, List<String>>? = null
)


data class CluterQueue(
        val name: String,
        val minWallTime: String,
        val maxWallTime: String
)
