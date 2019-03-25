package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.MetadataIdPathMapping
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import java.nio.file.Paths
import javax.inject.Inject

/**
 * Saves metadata id to idToPathMap file
 * @author Jakub Tucek
 */
class SaveMetadataIdPathMappingExecutor : TaskExecutor {


    @Inject
    private lateinit var serializationService: SerializationService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val metadataRootPath = Paths.get(metadata.configFile.general.metadataStoragePath)

        val mapping = serializationService.parseMetadataIdPathMapping(metadataRootPath)
                ?: MetadataIdPathMapping(emptyMap())

        val res = mapping.idToPathMap + Pair(
                metadata.metadataId!!,
                metadata.paths.metadataStoragePath!!.toAbsolutePath().toString()
        )
        serializationService.persistMetadataIdPathMapping(metadataRootPath, mapping.copy(idToPathMap = res))
        return metadata
    }

}