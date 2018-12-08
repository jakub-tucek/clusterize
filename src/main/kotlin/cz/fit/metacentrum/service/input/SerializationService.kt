package cz.fit.metacentrum.service.input

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

private val logger = KotlinLogging.logger {}

private const val metadataFileName: String = "metadata.yml"

/**
 * Serializes and dematerializes yaml files
 * @author Jakub Tucek
 */
class SerializationService {

    fun parseConfig(filePath: String): ConfigFile {
        val path = Paths.get(filePath)
        if (Files.notExists(path)) {
            throw IllegalArgumentException("Path ${path.toAbsolutePath()} of configuration file does not exists!")
        }

        try {
            val configurationFile = Files.newBufferedReader(path).use {
                mapper.readValue<ConfigFile>(it)
            }

            return configurationFile
        } catch (e: JsonProcessingException) {
            logger.error("Parsing yml failed", e)
            throw IllegalArgumentException("ConfigFile file probably does not exist in path ${path} or has invalid format", e)
        }
    }

    fun persistMetadata(metadata: ExecutionMetadata) {
        val metadataFile = metadata.paths.metadataStoragePath?.resolve(metadataFileName)
                ?: throw IllegalStateException("Metadata storage path not set")
        val writer = Files.newBufferedWriter(metadataFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        mapper.writeValue(writer, metadata)
    }

    fun parseMetadata(metadataPath: String): ExecutionMetadata? {
        val path = Paths.get(metadataPath).resolve(metadataFileName)
        if (!Files.exists(path)) {
            logger.error("Metadata file path ${path} does not exists.")
            return null
        }
        try {
            return Files.newBufferedReader(path).use {
                mapper.readValue<ExecutionMetadata>(it)
            }
        } catch (e: JsonProcessingException) {
            logger.error("Parsing metadata yml failed", e)
            throw IllegalArgumentException("Metadata file in path ${path} has probably invalid format")
        }
    }

    companion object {
        var mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
    }
}