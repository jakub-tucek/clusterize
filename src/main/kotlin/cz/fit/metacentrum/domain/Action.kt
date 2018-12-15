package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.meta.ExecutionMetadata


sealed class Action

// =====================================================================================================================

// action that submits new jobs to queue
sealed class ActionSubmit() : Action()

// extension of action submit that contains path to configuration file
data class ActionSubmitPath(val configFilePath: String) : ActionSubmit()

// extension of action submit that contains directly config file entity
data class ActionSubmitConfig(val configFile: ConfigFile) : ActionSubmit()


// =====================================================================================================================
// lists status of all executed tasks
data class ActionStatus(val metadataStoragePath: String? = null, val configFile: String? = null) : Action()
// =====================================================================================================================
// Resubmit action
data class ActionResubmitFailed(val metadata: ExecutionMetadata) : Action()

// Daemon action
data class ActionDaemon(val actionType: Type) : Action() {
    enum class Type {
        START, STOP
    }
}
// =====================================================================================================================
// displays help
object ActionHelp : Action()