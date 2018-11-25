package cz.fit.metacentrum.util

import java.nio.file.Files
import java.nio.file.Path

/**
 *
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
}