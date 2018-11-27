package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.ConfigFile
import java.nio.file.Path
import java.time.LocalDateTime

/**
 *
 * @author Jakub Tucek
 */
data class ExecutionMetadata(
        val configFile: ConfigFile,
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val metadataStoragePath: Path? = null,
        val storagePath: Path? = null,
        val sourcesPath: Path? = null,
        val iterationCombinations: List<Map<String, String>>? = null,
        val runScripts: List<ExecutionMetadataRunScript>? = null
)


data class ExecutionMetadataRunScript(val scriptPath: Path,
                                      val runId: Int, // identical to iteration combination index
                                      val pid: String? = null
)