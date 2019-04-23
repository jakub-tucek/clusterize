package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.config.ConfigResourceProfile
import cz.fit.metacentrum.domain.config.ConfigResourcesDetails
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class AutoResourceExecutor(private val clusterDetailsPath: Path = Paths.get(FileNames.defaultMetadataFolder)) : TaskExecutor {


    @Inject
    private lateinit var serializationService: SerializationService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val config = metadata.configFile.resources
        if (config.profile != ConfigResourceProfile.AUTO) return metadata

        val clusterDetails = serializationService.parseClusterDetails(clusterDetailsPath)

        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs are missing!")
        val benchmarkJob = jobs.last()

        val resourceType = config.resourceType
                ?: throw IllegalStateException("Automatic mode but does not specify resource type")

        val queues = clusterDetails.queueTypes.find { it.type == resourceType }
                ?: throw IllegalStateException("Queues for cluster does not contain selected type ${resourceType}")


        val longestQueue = queues.queues.last()


        val baseResourceDetails = ConfigResourcesDetails(
                walltime = longestQueue.minWallTime,
                mem = "1gb",
                scratchLocal = "1gb",
                ncpus = 1,
                chunks = 1
        )

        val benchmarkJobs = mutableListOf<ExecutionMetadataJob>()

        for (i in 1..2) {
            for (j in 1..2) {
                for (q in 1..2) {
                    val gpus = when (resourceType) {
                        "GPU" -> j
                        else -> null
                    }
                    benchmarkJobs.add(
                            benchmarkJob.copy(
                                    usedResources = baseResourceDetails.copy(
                                            chunks = i,
                                            ncpus = j,
                                            ngpus = gpus,
                                            mem = "${q}gb"
                                    )
                            )
                    )
                }
            }
        }

        return metadata.copy(
                jobs = jobs,
                internalQueue = jobs.dropLast(1)
        )
    }

}