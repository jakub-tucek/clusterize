package cz.fit.metacentrum.config

/**
 * @author Jakub Tucek
 */
const val appName = "clusterize"


const val userDateFormat = "dd/MM/YYYY hh:mm"

val cronJobPath = "${System.getProperty("user.home")}/.$appName/$appName/$appName"

object FileNames {
    // job info
    const val jobInfo = "job.info"
    // script
    const val innerScript = "inner_script.sh"
    const val stdOutFile = "std-out"

    val configDataFolderName = "${System.getProperty("user.home")}/.$appName"
    val defaultMetadataFolder = "/$configDataFolderName/metadataStorage"
    val cronLogFile = "$configDataFolderName/cron.log"
    val analysisFile = "$configDataFolderName/analysis.csv"
    val specificAnalysisFile = "$configDataFolderName/specific-analysis.csv"
    const val defaultClusterDetailsFile = "src/main/resources/metacentrum/source.yml"

    const val storageTaskFolderPrefix = "task-"
    val storageTaskFolderRegex = """^task-([0-9]+).*$""".toRegex()
}

object Configuration {
    const val cronInterval = "*/30 * * * *"
    const val maxCronLogSizeInMB: Long = 1L
}