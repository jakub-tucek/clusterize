package cz.fit.metacentrum.util

import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
internal class FileUtilsTest {


    @Test
    fun testPathReplace() {
        Assertions.assertThat(FileUtils.relativizePath("~/12"))
                .isEqualTo(System.getProperty("user.home") + "/12")
    }

    @Test
    fun copyDirectory() {
        val fileSystem = Jimfs.newFileSystem()
        val src = fileSystem.getPath("/src")
        Files.createDirectory(src)
        Files.createFile(src.resolve("file"))
        Files.createDirectory(src.resolve("dir"))
        Files.createFile(src.resolve("dir").resolve("file"))

        val res = fileSystem.getPath("/res")
        FileUtils.copyDirectory(src, res)


        Assertions.assertThat(res).exists()
        Assertions.assertThat(res.resolve("file")).exists()
        Assertions.assertThat(res.resolve("dir").resolve("file")).exists()
    }
}