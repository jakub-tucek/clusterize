package cz.fit.metacentrum.service.input

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import cz.fit.metacentrum.config.FileNames.configDataFolderName
import cz.fit.metacentrum.domain.AppConfiguration
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.JobInfo
import cz.fit.metacentrum.domain.meta.MetadataIdPathMapping
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

private const val metadataFileName: String = "metadata.yml"
private const val appConfigurationFileName: String = "app-configuration.yml"
private const val idPathMappingFileName: String = "metadata-id-to-path.yml"

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
        val timestampMetadata = metadata.copy(updateTime = LocalDateTime.now())
        val metadataFile = timestampMetadata.paths.metadataStoragePath?.resolve(metadataFileName)
                ?: throw IllegalStateException("Metadata storage path not set")
        // creates or rewrites existing file
        Files.newBufferedWriter(metadataFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                .use { mapper.writeValue(it, timestampMetadata) }
    }

    fun parseMetadata(metadataFolder: Path): ExecutionMetadata? {
        val path = metadataFolder.resolve(metadataFileName)
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

    fun parseAppConfiguration(): AppConfiguration? {
        val path = Paths.get(configDataFolderName).resolve(appConfigurationFileName)
        if (!Files.exists(path)) {
            logger.info("Configuration file does not exists. Returning null")
            return null
        }
        return try {
            Files.newBufferedReader(path).use {
                mapper.readValue<AppConfiguration>(it)
            }
        } catch (e: JsonProcessingException) {
            logger.error("Configuration has invalid structure. Returning null, file must be reinitialized", e)
            null
        }
    }

    fun persistAppConfiguration(appConfiguration: AppConfiguration) {
        val outPath = Paths.get(configDataFolderName).resolve(appConfigurationFileName)
        Files.createDirectories(outPath.parent)
        // creates or rewrites existing file
        Files.newBufferedWriter(outPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                .use { mapper.writeValue(it, appConfiguration) }
    }

    fun parseJobInfoFile(location: Path): JobInfo? {
        if (Files.notExists(location)) {
            logger.debug { "Job info file does not exists" }
            return null
        }
        return try {
            Files.newBufferedReader(location).use {
                mapper.readValue<JobInfo>(it)
            }
        } catch (e: JsonProcessingException) {
            logger.error("Unable to read job status info file", e)
            null
        }
    }

    fun parseMetadataIdPathMapping(location: Path): MetadataIdPathMapping? {
        val path = location.resolve(idPathMappingFileName)
        if (!Files.exists(path)) {
            logger.info { "Configuration path does not exist" }
            return null
        }
        return try {
            Files.newBufferedReader(path).use { mapper.readValue<MetadataIdPathMapping>(it) }
        } catch (e: JsonProcessingException) {
            logger.error("Parsing metadata id idToPathMap failed", e)
            throw IllegalStateException("Unable to parse metadata id idToPathMap")
        }
    }

    fun persistMetadataIdPathMapping(location: Path, mapping: MetadataIdPathMapping) {
        val path = location.resolve(idPathMappingFileName)

        Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                .use { mapper.writeValue(it, mapping) }
    }

    companion object {
        private var mapper: ObjectMapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
                .findAndRegisterModules()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
    }
}