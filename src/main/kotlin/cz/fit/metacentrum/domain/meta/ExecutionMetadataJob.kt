package cz.fit.metacentrum.domain.meta

import java.nio.file.Path


// Job that is or was submitted.
data class ExecutionMetadataJob(val jobPath: Path, // run path where output is saved
                                val jobId: Int, // identical to iteration combination index
                                val jobInfo: JobInfo // Additional info about job
)