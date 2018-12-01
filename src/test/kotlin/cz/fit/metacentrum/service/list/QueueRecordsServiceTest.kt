package cz.fit.metacentrum.service.list

import cz.fit.metacentrum.KotlinMockito
import cz.fit.metacentrum.domain.CommandOutput
import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.service.api.ShellService
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
internal class QueueRecordsServiceTest {

    @InjectMocks
    private lateinit var queueRecordsService: QueueRecordsService

    @Mock
    private lateinit var shellService: ShellService


    @BeforeEach
    fun setUp() {
        Mockito.`when`(shellService.runCommand(KotlinMockito.any())).thenReturn(
                CommandOutput(
                        """
1.Q jobName username         0 Q q_1d
2.Q jobName username2         0 Q q_1d
                        """.trimIndent(), 0, ""
                )
        )
    }

    @Test
    fun testResultIsProperlyMapped() {
        val res = queueRecordsService.retrieveQueueForUser("username")
        Assertions.assertThat(res)
                .hasSize(1)
                .contains(QueueRecord("1", "jobName", "username", "0",
                        QueueRecord.InternalState.Q,
                        QueueRecord.State.QUEUED,
                        "queueName"))
    }

    @Test
    fun testThatResultIsCached() {
        val res = queueRecordsService.retrieveQueueForUser("username")
        val res2 = queueRecordsService.retrieveQueueForUser("username")

        Assertions.assertThat(res).isEqualTo(res2)
        Mockito.verify(shellService, Mockito.times(1)).runCommand(KotlinMockito.any())
    }

    @Test
    fun testTharResultIsOkForDifferentUser() {
        val res = queueRecordsService.retrieveQueueForUser("username")
        val res2 = queueRecordsService.retrieveQueueForUser("username2")

        Assertions.assertThat(res)
                .extracting<String> { it.username }
                .contains("username")
        Assertions.assertThat(res2)
                .extracting<String> { it.username }
                .contains("username2")
    }
}