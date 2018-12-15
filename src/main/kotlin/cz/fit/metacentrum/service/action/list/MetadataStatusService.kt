package cz.fit.metacentrum.service.action.list

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataComparator
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
    private lateinit var checkQueueExecutor: CheckQueueExecutor

    fun retrieveMetadata(metadataPath: Path): List<ExecutionMetadata> {
        return Files.list(metadataPath)
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
    }

    /**
     * Checks given metadata status by calling check queue executor. Returns only changed metadata objects.
     */
    fun updateMetadataState(originalMetadata: ExecutionMetadata): ExecutionMetadata {
        return checkQueueExecutor.execute(originalMetadata)
    }

    fun isUpdatedMetadata(originalMetadataList: List<ExecutionMetadata>,
                          updatedMetadata: ExecutionMetadata): Boolean {
        return originalMetadataList.contains(updatedMetadata)
    }
}