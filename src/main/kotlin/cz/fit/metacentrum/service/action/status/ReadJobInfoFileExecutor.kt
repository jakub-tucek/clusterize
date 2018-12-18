package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ReadJobInfoFileExecutor : TaskExecutor {

    @Inject
    private lateinit var serializationService: SerializationService


    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        // no new information can be obtained and metadata should already contain job info
        if (metadata.isFinished()) {
            return metadata
        }
        val storagePath = metadata.paths.storagePath ?: throw IllegalStateException("Storage path not set")
        val readJobInfo = metadata.jobs?.map {
            val jobInfo = serializationService.parseJobInfoFile(storagePath.resolve(FileNames.jobInfo))
            if (jobInfo == null) {
                it
            } else {
                it.copy(jobInfo = jobInfo)
            }
        }
        return metadata.copy(jobs = readJobInfo)
    }

}