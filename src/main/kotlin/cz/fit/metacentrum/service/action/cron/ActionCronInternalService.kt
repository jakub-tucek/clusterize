package cz.fit.metacentrum.service.action.cron

import cz.fit.metacentrum.config.Configuration
import cz.fit.metacentrum.config.FileNames.cronLogFile
import cz.fit.metacentrum.domain.ActionCronStartInternal
import cz.fit.metacentrum.service.api.ActionService
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject


class ActionCronStartInternalService : ActionService<ActionCronStartInternal> {

    @Inject
    private lateinit var watcherService: WatcherService

    override fun processAction(argumentAction: ActionCronStartInternal) {
        checkLogSize()
        watcherService.checkMetadataStatus()
    }

    private fun checkLogSize() {
        val p = Paths.get(cronLogFile)
        if (Files.exists(p)) {
            val size = Files.size(Paths.get(cronLogFile))
            if (size * 1_000_000 > Configuration.maxCronLogSizeInMB) {
                Files.delete(p)
            }
        }
    }

}