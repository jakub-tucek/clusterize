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
    // outs
    const val stdOutLog = "stdout.log"
    const val stdErrLog = "stderr.log"
    const val stdJobLog = "stdout_job.log"
    // script
    const val innerScript = "inner_script.sh"

    const val sourcesFolder = "sources"

    val configDataFolderName = "${System.getProperty("user.home")}/.$appName"
    val defaultMetadataFolder = "/$configDataFolderName/metadataStorage"
    val cronLogFile = "$configDataFolderName/cron.log"
    const val defaultSourceFile = "src/main/resources/metacentrum/source.yml"

    const val storageTaskFolderPrefix = "task-"
    val storageTaskFolderRegex = """^task-([0-9]+).*$""".toRegex()
}

object Configuration {
    const val cronInterval = "*/30 * * * *"
    const val maxCronLogSizeInMB: Long = 1L
}