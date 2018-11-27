package cz.fit.metacentrum.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.ConfigFile
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

private val logger = KotlinLogging.logger {}

/**
 *
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
            throw IllegalArgumentException(
                    "ConfigFile file has invalid format. Check if file has proper format. ${e.message}",
                    e)
        }
    }

    fun persistMetadata(metadata: ExecutionMetadata) {
        val metadataFile = metadata.metadataStoragePath?.resolve("metadata.yml")
                ?: throw IllegalStateException("Metadata storage path not set")

        val writer = Files.newBufferedWriter(metadataFile, StandardOpenOption.CREATE_NEW)
        mapper.writeValue(writer, metadata)
    }

    companion object {
        var mapper = ObjectMapper(YAMLFactory()).registerKotlinModule() // Enable YAML parsing
    }
}