package cz.fit.metacentrum.config

/**
 *
 * @author Jakub Tucek
 */
const val appName = "clusterize"


const val userDateFormat = "dd/MM/YYYY hh:mm"



object FileNames {
    val statusLog = "status.log"
    val pidFile = "pid.log"
    val stdOutLog = "stdout.log"
    val stdErrLog = "stderr.log"
    val stdJobLog = "stdout_job.log"
    val innerScript = "inner_script.sh"

    val sourcesFolder = "sources"

    val configDataFolderName = "${System.getProperty("user.home")}/.$appName"
    val defaultMetadataFolder = "/$configDataFolderName/metadataStorage"
    val daemonLogFile = "$configDataFolderName/daemon.log"

    val storageTaskFolderPrefix = "task-"
    val storageTaskFolderRegex = """^task-([0-9]+).*$""".toRegex()
}