package cz.fit.metacentrum.service.action.status

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionStatusExecutorsToken
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataComparator
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.util.ConsoleWriter
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.streams.toList

/**
 * Metadata status service.
 * @author Jakub Tucek
 */
class MetadataStatusService {
    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    @Named(actionStatusExecutorsToken)
    private lateinit var executors: Set<@JvmSuppressWildcards TaskExecutor>

    fun retrieveMetadata(metadataPath: Path): List<ExecutionMetadata> {
        if (Files.notExists(metadataPath)) return emptyList()

        val updatedMetadata = Files.list(metadataPath)
                .filter { Files.isDirectory(it) }
                .map {
                    val res = serializationService.parseMetadata(it)
                    if (res == null) {
                        ConsoleWriter.writeStatus("Folder ${it.toString()} does not contain metadata file")
                    }
                    res
                }
                .sorted(ExecutionMetadataComparator::compare)
                .toList()
                .filterNotNull()

        return updatedMetadata
    }

    /**
     * Checks given metadata status by calling executors. Returns changed metadata objects.
     */
    fun updateMetadataState(originalMetadata: ExecutionMetadata): ExecutionMetadata {
        return executors.fold(originalMetadata) { metadata, executor ->
            executor.execute(metadata)
        }
    }

    fun isUpdatedMetadata(originalMetadataList: List<ExecutionMetadata>,
                          updatedMetadata: ExecutionMetadata): Boolean {
        return !originalMetadataList.contains(updatedMetadata)
    }
}