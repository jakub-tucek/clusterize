package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.service.TestData
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class MatlabScriptsExecutorTest {


    val ex = MatlabScriptsExecutor()

    @Test
    fun testMatlabScriptGeneration() {
        ex.execute(TestData.metadata)
    }
}