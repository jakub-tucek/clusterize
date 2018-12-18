package cz.fit.metacentrum.domain.meta

import java.nio.file.Path

/**
 * Contains paths used for execution
 * @author Jakub Tucek
 */
data class ExecutionMetadataPath(
        val metadataStoragePath: Path? = null,
        val storagePath: Path? = null,
        val sourcesPath: Path? = null
)
