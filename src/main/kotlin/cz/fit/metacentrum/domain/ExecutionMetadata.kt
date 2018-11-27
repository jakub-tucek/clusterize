package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.ConfigFile
import java.nio.file.Path

/**
 *
 * @author Jakub Tucek
 */
data class ExecutionMetadata(
        val metadataStoragePath: Path? = null,
        val storagePath: Path? = null,
        val sourcesPath: Path? = null,
        val configFile: ConfigFile,
        val iterationCombinations: List<Map<String, String>>? = null
)