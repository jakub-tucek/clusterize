package cz.fit.metacentrum.service.action.status.ex

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.JobInfo
import cz.fit.metacentrum.domain.meta.JobInfoFile
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import javax.inject.Inject

/**
 * Reads jobInfo file for each metadata's job and saves it to metadata.
 * @author Jakub Tucek
 */
class ReadJobInfoFileExecutor : TaskExecutor {

    @Inject
    private lateinit var serializationService: SerializationService


    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        // no new information can be obtained and metadata should already contain job info
        if (metadata.currentState.isFinishing()) {
            return metadata
        }
        val readJobInfo = metadata.jobs?.map {
            val jobInfoFile = serializationService.parseJobInfoFile(it.jobPath.resolve(FileNames.jobInfo))
            if (jobInfoFile == null) {
                it
            } else {
                val mergedJobInfo = mergeJobInfo(it.jobInfo, jobInfoFile)
                if (mergedJobInfo == it.jobInfo) {
                    it.copy(jobInfo = mergedJobInfo)
                } else {
                    it
                }
            }
        }
        return metadata.copy(jobs = readJobInfo)
    }


    private fun mergeJobInfo(jobInfo: JobInfo, jobInfoFile: JobInfoFile?): JobInfo {
        if (jobInfoFile == null) {
            return jobInfo
        }
        return jobInfo.copy(
                start = jobInfoFile.start,
                end = jobInfoFile.end,
                status = jobInfoFile.status
        )
    }
}