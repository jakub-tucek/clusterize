package cz.fit.metacentrum.domain.management


/**
 *
 */
data class QueueDescription(
        val name: String,
        val queues: List<String>,
        val queueType: QueueDescriptionType? = null
)

sealed class QueueDescriptionType

data class QueueTypeGpu(
        val gpuCaps: Array<String>
) : QueueDescriptionType()
