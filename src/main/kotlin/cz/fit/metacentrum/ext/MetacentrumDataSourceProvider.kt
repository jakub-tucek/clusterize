package cz.fit.metacentrum.ext

import cz.fit.metacentrum.domain.management.QueueDataSource
import cz.fit.metacentrum.domain.management.QueueInformation
import cz.fit.metacentrum.domain.management.QueueJobsInformation
import mu.KotlinLogging
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class MetacentrumDataSourceProvider : DataSourceProvider {

    override fun parseDatasource(dataSource: QueueDataSource): List<QueueInformation> {
        val document = retrieveDocument(dataSource.sourceUrl)
        val table = document.body().select("table.queue")
        return when (table.size) {
            0 -> {
                throw IllegalStateException("Downloaded document has no table")
            }
            else -> parseTable(table.first())
        }
    }

    private fun parseTable(table: Element): List<QueueInformation> {
        val dataRows = table.select("tr").drop(2)
        return dataRows.map { mapRowToQueueInfo(it) }
    }

    private fun mapRowToQueueInfo(it: Element): QueueInformation {
        val cols = it.select("td")
        if (cols.size != 11) {
            throw IllegalStateException("Unexpected number of columns found: ${cols.size}")
        }
        val (minWallTime, maxWallTime) = cols[3].text().trim().split(" - ")
        val (running) = cols[5].text().split("/")

        return QueueInformation(
                queue = cols[0].text(),
                states = cols[1].text(),
                priority = cols[2].text().toInt(),
                minWallTime = minWallTime,
                maxWallTime = maxWallTime,
                jobStateInformation = QueueJobsInformation(
                        queuedJobs = cols[4].text().toInt(),
                        runningJobs = running.trim().toInt(),
                        completedJobs = cols[6].text().toInt(),
                        totalJobs = cols[7].text().toInt(),
                        maxJobsPerUser = cols[8].text().toIntOrNull()
                ),
                maxCPUsPerUser = cols[9].text().toIntOrNull()
        )
    }

    private fun retrieveDocument(sourceUrl: String): Document {
        try {
            val username: String? = System.getenv("METACENTRUM_USERNAME")
            val password: String? = System.getenv("METACENTRUM_PASSWORD")
            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                throw IllegalArgumentException("Username or password are not set in METACENTRUM_USERNAME/METACENTRUM_PASSWORD")
            }
            val login = "$username:$password"
            val base64login = String(Base64.getEncoder().encode(login.toByteArray()))

            val loginResponse = Jsoup
                    .connect(sourceUrl)
                    .method(Connection.Method.GET)
                    .header("Authorization", "Basic $base64login")
                    .execute()

            val firstFormSubmit = postLoginForm(loginResponse)

            val retrieveSecondForm = Jsoup.connect(sourceUrl)
                    .timeout(30000)
                    .method(Connection.Method.GET)
                    .cookies(firstFormSubmit.cookies())
                    .execute()

            val secondFormSubmit = postLoginForm(retrieveSecondForm)

            val document = Jsoup.connect(sourceUrl)
                    .timeout(30000)
                    .method(Connection.Method.GET)
                    .followRedirects(true)
                    .cookies(secondFormSubmit.cookies())
                    .get()
            return document
        } catch (e: Exception) {
            logger.error("Retrieval of $sourceUrl failed. Cannot obtain document", e)
            throw e
        }
    }

    private fun postLoginForm(loginResponse: Connection.Response): Connection.Response {
        val authorizationResponse = loginResponse.parse()
        val postLink = authorizationResponse.select("form")
                .attr("action")
        val samlResponse = authorizationResponse.select("input[name=SAMLResponse]")
                .attr("value")

        val firstFormSubmit = Jsoup.connect(postLink)
                .method(Connection.Method.POST)
                .data("SAMLResponse", samlResponse)
                .execute()
        return firstFormSubmit
    }
}