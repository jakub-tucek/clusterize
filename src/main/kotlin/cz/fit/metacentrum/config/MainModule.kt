package cz.fit.metacentrum.config

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import cz.fit.metacentrum.service.MainService
import cz.fit.metacentrum.service.ShellServiceDockerProxy
import cz.fit.metacentrum.service.ShellServiceImpl
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.CommandLineParser
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.service.input.validator.ConfigValidationService
import cz.fit.metacentrum.service.input.validator.IterationConfigValidator
import cz.fit.metacentrum.service.list.ActionListService
import cz.fit.metacentrum.service.list.CheckQueueExecutor
import cz.fit.metacentrum.service.list.FailedJobFinderService
import cz.fit.metacentrum.service.list.MetadataInfoPrinter
import cz.fit.metacentrum.service.submit.ActionSubmitService
import cz.fit.metacentrum.service.submit.executor.*


class MainModule : AbstractModule() {

    override fun configure() {
        bind(MainService::class.java)

        bind(SerializationService::class.java)
        bind(CommandLineParser::class.java)

        // validator
        bind(ConfigValidationService::class.java)
        bind(IterationConfigValidator::class.java)

        bindExecutors()

        bind(ActionSubmitService::class.java)
        bind(ActionListService::class.java)

        if (ProfileConfiguration.isDev()) {
            val dockerShellServiceProxy = ShellServiceDockerProxy(ShellServiceImpl())
            bind(ShellService::class.java).toInstance(dockerShellServiceProxy)
        } else {
            bind(ShellService::class.java).to(ShellServiceImpl::class.java)
        }
        bind(FailedJobFinderService::class.java)
        bind(MetadataInfoPrinter::class.java)
    }

    private fun bindExecutors() {
        val matlabBinder = Multibinder.newSetBinder(binder(), TaskExecutor::class.java)

        matlabBinder.addBinding().to(ResolvePathExecutor::class.java)
        matlabBinder.addBinding().to(UsernameResolverExecutor::class.java)
        matlabBinder.addBinding().to(InitOutputPathExecutor::class.java)
        matlabBinder.addBinding().to(CopySourcesFilesExecutor::class.java)
        matlabBinder.addBinding().to(IterationExecutor::class.java)
        matlabBinder.addBinding().to(MatlabScriptsExecutor::class.java)
        matlabBinder.addBinding().to(SubmitExecutor::class.java)


        bind(CheckQueueExecutor::class.java)
    }
}