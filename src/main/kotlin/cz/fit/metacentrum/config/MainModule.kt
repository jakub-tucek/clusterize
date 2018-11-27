package cz.fit.metacentrum.config

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import cz.fit.metacentrum.service.*
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.executor.*
import cz.fit.metacentrum.service.validator.ConfigValidationService
import cz.fit.metacentrum.service.validator.IterationConfigValidator


class MainModule : AbstractModule() {

    override fun configure() {
        bind(MainService::class.java)

        bind(ConfigFileParser::class.java)
        bind(CommandLineParser::class.java)

        // validator
        bind(ConfigValidationService::class.java)
        bind(IterationConfigValidator::class.java)

        bindExecutors()

        bind(ActionSubmitService::class.java)
        bind(ShellService::class.java)
    }

    private fun bindExecutors() {
        val matlabBinder = Multibinder.newSetBinder(binder(), TaskExecutor::class.java)

        matlabBinder.addBinding().to(ResolvePathExecutor::class.java)
        matlabBinder.addBinding().to(InitOutputPathExecutor::class.java)
        matlabBinder.addBinding().to(CopyMatlabFilesExecutor::class.java)
        matlabBinder.addBinding().to(IterationExecutor::class.java)
        matlabBinder.addBinding().to(MatlabScriptsExecutor::class.java)
    }
}