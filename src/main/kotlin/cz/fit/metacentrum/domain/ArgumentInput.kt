package cz.fit.metacentrum.domain


sealed class Action

data class ActionSubmit(val configFile: String) : Action()

data class ActionList(val metadataStoragePath: String? = null, val configFile: String? = null) : Action()

object ActionHelp : Action()