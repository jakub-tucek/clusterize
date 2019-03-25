package cz.fit.metacentrum.util

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/**
 * File utilities
 * @author Jakub Tucek
 */
object FileUtils {

    fun relativizePath(path: String): String {
        return path.replace(Regex("^~"), System.getProperty("user.home"))
    }

    fun deleteFolder(path: Path) {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(Files::delete)
    }

    fun copyDirectory(source: Path, dest: Path) {
        Files.walkFileTree(source, CopyDir(source, dest))
    }
}

class CopyDir(private val sourceDir: Path, val targetDir: Path) : SimpleFileVisitor<Path>() {

    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        val targetFile = targetDir.resolve(sourceDir.relativize(file))
        Files.copy(file, targetFile)
        return FileVisitResult.CONTINUE
    }

    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        val newDir = targetDir.resolve(sourceDir.relativize(dir))
        Files.createDirectory(newDir)
        return FileVisitResult.CONTINUE
    }
}