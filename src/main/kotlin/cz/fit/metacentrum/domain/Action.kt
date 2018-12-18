package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.meta.ExecutionMetadata

// Represents type of action that user wants to do. Usually specified via command line argument
sealed class Action

// =====================================================================================================================

// action that submits new jobs to queue
sealed class ActionSubmit() : Action()

// extension of action submit that contains path to configuration file
data class ActionSubmitPath(val configFilePath: String) : ActionSubmit()

// extension of action submit that contains directly config file entity
data class ActionSubmitConfig(val configFile: ConfigFile) : ActionSubmit()


// =====================================================================================================================
// lists status of all executed tasks and prints them to console
data class ActionStatus(val metadataStoragePath: String? = null, val configFile: String? = null) : Action()

// =====================================================================================================================
// Resubmit action
data class ActionResubmitFailed(val metadata: ExecutionMetadata) : Action()

// Cron action
data class ActionCron(val actionType: Type) : Action() {
    enum class Type {
        START, STOP, RESTART
    }
}

// Internal action that is executed by cron. Checks status and sends email if needed
object ActionCronStartInternal : Action()

// =====================================================================================================================
// displays help
object ActionHelp : Action()