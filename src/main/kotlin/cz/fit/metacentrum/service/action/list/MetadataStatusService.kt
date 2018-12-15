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
     * Checks given metadata status by calling check queue executor.
     */
    fun updateMetadataState(originalMetadata: List<ExecutionMetadata>): List<ExecutionMetadata> {
        return originalMetadata
                .map { checkQueueExecutor.execute(it) }
    }

    fun retrieveChangedStateMetadataState(originalMetadata: List<ExecutionMetadata>,
                                          listedMetadata: List<ExecutionMetadata>): List<ExecutionMetadata> {
        return listedMetadata.filter { !originalMetadata.contains(it) }
    }
}