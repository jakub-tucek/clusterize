package cz.fit.metacentrum.service

import cz.fit.metacentrum.KotlinMockito
import cz.fit.metacentrum.domain.CommandOutput
import cz.fit.metacentrum.domain.QueueRecord
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

/**
 * @author Jakub Tucek
 */
@ExtendWith(MockitoExtension::class)
internal class QueueServiceTest {

    @InjectMocks
    private lateinit var queueService: QueueService

    @Mock
    private lateinit var shellService: ShellService


    @BeforeEach
    fun setUp() {
        Mockito.`when`(shellService.runCommand(KotlinMockito.any())).thenReturn(
                CommandOutput(
                        """
JobId jobName username         0 Q q_1d
                        """.trimIndent(), 0, ""
                )
        )
    }

    @Test
    fun testCacheIsProperlyMapped() {
        val res = queueService.retrieveQueueForUser()
        Assertions.assertThat(res)
                .hasSize(1)
                .contains(QueueRecord("JobId", "jobName", "username", "0", "Q", "q_1d"))
    }
}