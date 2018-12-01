package cz.fit.metacentrum.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class ShellServiceImplTest {

    private val shell = ShellServiceImpl()

    @Test
    fun testRunningSimpleCommand() {
        val (output, status, errOutput) = shell.runCommand("echo 123")

        Assertions.assertThat(status).isEqualTo(0)
        Assertions.assertThat(errOutput).isEmpty()
        Assertions.assertThat(output).isEqualTo("123")
    }

    @Test
    fun testRunFailed() {
        val (output, status, errOutput) = shell.runCommand("ls -2")

        Assertions.assertThat(status).isEqualTo(2)
        Assertions.assertThat(errOutput).contains("ls --help")
        Assertions.assertThat(output).isEmpty()
    }

    @Test
    @Disabled
    fun runDocker() {
        val runCommand = shell
                .runCommand("docker exec --user pbsuser pbs bash -c \"echo \$PATH\" ")

        println(runCommand)
    }
}