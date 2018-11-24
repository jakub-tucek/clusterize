package cz.fit.metacentrum.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class FileUtilTest {


    @Test
    fun testPathReplace() {
        Assertions.assertThat(FileUtil.relativizePath("~/12"))
                .toString()
                .equals(System.getProperty("user.home") + "/12")
    }
}