package cz.fit.metacentrum.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class QueueUtilsTest {
    @Test
    fun testValidPidExtraction() {
        Assertions.assertThat(
                QueueUtils.extractPid("8026136.arien-pro.ics.muni.cz")
        ).isEqualTo("8026136")
    }

}