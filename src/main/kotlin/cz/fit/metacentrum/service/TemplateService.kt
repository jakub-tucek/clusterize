package cz.fit.metacentrum.service

import com.github.mustachejava.DefaultMustacheFactory
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path

/**
 * Template creation service
 * @author Jakub Tucek
 */
class TemplateService {


    fun write(templateName: String, outPath: Path, data: Any) {
        val mustache = factory.compile(templateName)

        if (Files.notExists(outPath)) Files.createFile(outPath)


        FileWriter(outPath.toFile()).use {
            mustache.execute(it, data).flush()
        }
    }

    companion object {
        private val factory = DefaultMustacheFactory()
    }
}