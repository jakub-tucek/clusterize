package cz.fit.metacentrum.domain

import java.nio.file.Path

/**
 *
 * @author Jakub Tucek
 */
data class ExecutionMetadata(
        val executionOutputPath: Path? = null,
        val configFile: ConfigFile
)