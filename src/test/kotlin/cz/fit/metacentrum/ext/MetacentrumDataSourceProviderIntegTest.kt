package cz.fit.metacentrum.ext

import cz.fit.metacentrum.domain.management.QueueDataSource
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class MetacentrumDataSourceProviderIntegTest {

    private val metacentrumDataSourceProvider: MetacentrumDataSourceProvider = MetacentrumDataSourceProvider()


    @Ignore
    @Test
    fun testThatParsingWorks() {
        val result = metacentrumDataSourceProvider.parseDatasource(
                QueueDataSource("",
                        "https://metavo.metacentrum.cz/pbsmon2/queues/list",
                        "",
                        emptyList()
                )
        )
        println(result)
    }

}