package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.domain.CommandOutput
import cz.fit.metacentrum.service.api.ShellService
import org.assertj.core.api.Assertions
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
internal class UsernameResolverExecutorTest {

    @InjectMocks
    private lateinit var usernameResolverExecutor: UsernameResolverExecutor
    @Mock
    private lateinit var shellService: ShellService

    @Test
    fun checkRetrievingUsername() {
        Mockito.`when`(shellService.runCommand("whoami")).thenReturn(
                CommandOutput("myName", 0, "")
        )
        val res = usernameResolverExecutor.execute(TestData.metadata)
        Assertions.assertThat(res.submittingUsername).isEqualTo("myName")
    }
}