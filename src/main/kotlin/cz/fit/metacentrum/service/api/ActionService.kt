package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.Action

/**
 * Generic action type handler that runs and processes executor steps
 * @property T defines action type
 */
interface ActionService<T : Action> {

    /**
     * Processes action based on it's type T
     */
    fun processAction(argumentAction: T)

}