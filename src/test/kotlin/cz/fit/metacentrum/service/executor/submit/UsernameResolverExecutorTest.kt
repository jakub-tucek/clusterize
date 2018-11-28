package cz.fit.metacentrum.service.executor.submit

import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class UsernameResolverExecutorTest {


    @Test
    fun checkRetrievingUsername() {
        val res = UsernameResolverExecutor().execute(TestData.metadata)
        Assertions.assertThat(res.submittingUsername).isEqualTo(System.getProperty("user.name"))
    }
}