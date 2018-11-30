package cz.fit.metacentrum.domain.meta

import java.nio.file.Path


// Job that is or was submitted. Submitted job has set PID.
data class ExecutionMetadataJob(val scriptPath: Path,
                                val runId: Int, // identical to iteration combination index
                                val pid: String? = null
)