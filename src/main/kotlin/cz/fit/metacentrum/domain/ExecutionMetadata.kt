package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.ConfigFile
import java.nio.file.Path

/**
 *
 * @author Jakub Tucek
 */
data class ExecutionMetadata(
        val executionOutputPath: Path? = null,
        val configFile: ConfigFile,
        val iterationCombinations: List<Map<String, String>>? = null
)