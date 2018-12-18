package cz.fit.metacentrum.domain.template

import cz.fit.metacentrum.domain.config.ConfigResources
import java.nio.file.Path

/**
 * Represents data for template used in mail when notifying user about status change.
 * @author Jakub Tucek
 */
data class StatusMailTemplateData(
        // mail
        val from: String,
        val to: String,
        val subject: String,
        // config
        val taskName: String,
        val creationTime: String,
        val updateTime: String,
        val outputPath: Path,
        val resources: ConfigResources,
        // actual state
        val stateBody: String
)