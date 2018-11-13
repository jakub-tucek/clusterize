package cz.fit.metacentrum.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import cz.fit.metacentrum.domain.ConfigurationFile
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class ConfigFileParser {

    fun parse(filePath: String): ConfigurationFile {
        val path = Paths.get(filePath)
        if (Files.notExists(path)) {
            throw IllegalArgumentException("Path ${path.toAbsolutePath()} to configuration file does not exists!")
        }

        try {
            val (variables) = Files.newBufferedReader(path).use {
                mapper.readValue(it, ConfigurationFileNullable::class.java)
            }
            return ConfigurationFile(
                    variables ?: emptyMap<String, String>()
            )
        } catch (e: JsonProcessingException) {
            logger.error("Parsing yml failed", e)
            throw IllegalArgumentException("Configuration file has invalid format. Check if file has proper format.", e)
        }
    }

    companion object {
        val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing

        init {
            mapper.registerModule(KotlinModule()) // Enable Kotlin support
        }
    }

    data class ConfigurationFileNullable(val variables: Map<String, String>?)
}