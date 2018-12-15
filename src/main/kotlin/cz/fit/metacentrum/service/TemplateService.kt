package cz.fit.metacentrum.service

import com.github.mustachejava.DefaultMustacheFactory
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path

/**
 *
 * @author Jakub Tucek
 */
class TemplateService {


    fun write(templateName: String, outPath: Path, data: Any) {
        val mustache = factory.compile(templateName)

        Files.createFile(outPath)
        val writer = FileWriter(outPath.toFile())
        mustache.execute(writer, data).flush()
    }


    companion object {
        private val factory = DefaultMustacheFactory()
    }
}