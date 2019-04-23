package cz.fit.metacentrum.service.input

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import cz.fit.metacentrum.config.FileNames.configDataFolderName
import cz.fit.metacentrum.domain.AppConfiguration
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.management.ClusterDetails
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.JobInfoFile
import cz.fit.metacentrum.domain.meta.MetadataIdPathMapping
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

private const val metadataFileName: String = "metadata.yml"
private const val appConfigurationFileName: String = "app-configuration.yml"
private const val idPathMappingFileName: String = "metadata-id-to-path.yml"
private const val versionFileName: String = "version.yml"

/**
 * Serializes and dematerializes yaml files
 * @author Jakub Tucek
 */
class SerializationService {

    fun parseConfig(filePath: String): ConfigFile {
        val path = Paths.get(filePath)
        return readFile<ConfigFile>(path)
                ?: throw IllegalArgumentException("ConfigFile file probably does not exist in path ${path} or has invalid format")
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
        return readFile<ExecutionMetadata>(path)
    }

    fun parseAppConfiguration(): AppConfiguration? {
        val path = Paths.get(configDataFolderName).resolve(appConfigurationFileName)
        return readFile<AppConfiguration>(path)
    }

    fun persistAppConfiguration(appConfiguration: AppConfiguration) {
        val outPath = Paths.get(configDataFolderName).resolve(appConfigurationFileName)
        Files.createDirectories(outPath.parent)
        // creates or rewrites existing file
        Files.newBufferedWriter(outPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                .use { mapper.writeValue(it, appConfiguration) }
    }

    fun parseJobInfoFile(location: Path): JobInfoFile? {
        return readFile<JobInfoFile>(location)
    }

    fun parseMetadataIdPathMapping(location: Path): MetadataIdPathMapping? {
        return readFile<MetadataIdPathMapping>(location.resolve(idPathMappingFileName))
    }

    fun persistMetadataIdPathMapping(location: Path, mapping: MetadataIdPathMapping) {
        val path = location.resolve(idPathMappingFileName)

        Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                .use { mapper.writeValue(it, mapping) }
    }

    fun parseClusterDetails(location: Path): ClusterDetails {
        return readFile<ClusterDetails>(location) ?: throw IllegalStateException("Unable to parse file in $location")
    }

    private inline fun <reified T> readFile(path: Path): T? {
        if (!Files.exists(path)) {
            logger.info { "Path $path does not exist" }
            return null
        }
        return try {
            Files.newBufferedReader(path).use { mapper.readValue<T>(it) }
        } catch (e: JsonProcessingException) {
            logger.error("Parsing file in $path failed.", e)
            return null
        }
    }

    fun readVersionFile(): Map<String, String>? {
        try {
            return this.javaClass.classLoader.getResourceAsStream(versionFileName)
                    .use { mapper.readValue<Map<String, String>>(it) }
        } catch (e: IOException) {
            logger.error("Parsing version file failed", e)
            return null
        }
    }

    companion object {
        private var mapper: ObjectMapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
                .findAndRegisterModules()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}