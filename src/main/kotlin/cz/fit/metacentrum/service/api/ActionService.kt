package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.Action

/**
 * Generic action type handler
 */
interface ActionService<T : Action> {
    fun processAction(argumentAction: T)
}