package cz.fit.metacentrum.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class FileUtilsTest {


    @Test
    fun testPathReplace() {
        Assertions.assertThat(FileUtils.relativizePath("~/12"))
                .isEqualTo(System.getProperty("user.home") + "/12")
    }
}