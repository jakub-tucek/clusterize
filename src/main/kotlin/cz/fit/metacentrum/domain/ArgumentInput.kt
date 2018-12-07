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
// lists all tasks and its status based on executed jobs
data class ActionList(val metadataStoragePath: String? = null, val configFile: String? = null) : Action()

// =====================================================================================================================
// Resubmit action
data class ActionResubmitFailed(val metadata: ExecutionMetadata) : Action()

// =====================================================================================================================
// displays help
object ActionHelp : Action()