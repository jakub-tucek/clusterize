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
                                                            Req'd  Req'd   Elap
Job ID          username Queue    Jobname    SessID NDS TSK Memory Time  S Time
--------------- -------- -------- ---------- ------ --- --- ------ ----- - -----
81.pbs          pbsuser  workq    oneCPUjob    5736   1   1    1gb 04:00 R 00:00
82.pbs          pbsuser2  workq    oneCPUjob    5778   1   1    1gb 04:00 R 00:00

                        """.trimIndent(), 0, ""
                )
        )
    }

    @Test
    fun testResultIsProperlyMapped() {
        val res = queueRecordsService.retrieveQueueForUser("pbsuser")
        Assertions.assertThat(res)
                .hasSize(1)
                .contains(QueueRecord("81", "pbsuser", "workq", "oneCPUjob",
                        "5736",
                        "1",
                        "1",
                        "1gb",
                        "04:00",
                        QueueRecord.InternalState.R,
                        "00:00",
                        QueueRecord.State.RUNNING
                ))
    }

    @Test
    fun testThatResultIsCached() {
        val res = queueRecordsService.retrieveQueueForUser("pbsuser")
        val res2 = queueRecordsService.retrieveQueueForUser("pbsuser")

        Assertions.assertThat(res).isEqualTo(res2)
        Mockito.verify(shellService, Mockito.times(1)).runCommand(KotlinMockito.any())
    }

    @Test
    fun testTharResultIsOkForDifferentUser() {
        val res = queueRecordsService.retrieveQueueForUser("pbsuser")
        val res2 = queueRecordsService.retrieveQueueForUser("pbsuser2")

        Assertions.assertThat(res)
                .extracting<String> { it.username }
                .contains("pbsuser")
        Assertions.assertThat(res2)
                .extracting<String> { it.username }
                .contains("pbsuser2")
    }
}