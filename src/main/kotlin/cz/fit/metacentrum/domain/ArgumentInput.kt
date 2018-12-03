package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.ConfigFile


sealed class Action

sealed class ActionSubmit() : Action()
// extension of action submit that contains path to configuration file
data class ActionSubmitPath(val configFilePath: String) : ActionSubmit()

// extension of action submit that contains directly config file entity
data class ActionSubmitConfig(val configFile: ConfigFile) : ActionSubmit()

data class ActionList(val metadataStoragePath: String? = null, val configFile: String? = null) : Action()

object ActionHelp : Action()