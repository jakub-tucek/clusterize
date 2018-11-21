package cz.fit.metacentrum.domain


sealed class Action

data class ActionSubmit(val configFile: String) : Action()
object ActionHelp : Action()