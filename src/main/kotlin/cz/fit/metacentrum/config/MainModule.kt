package cz.fit.metacentrum.config

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.MainService
import cz.fit.metacentrum.service.ShellServiceDockerProxy
import cz.fit.metacentrum.service.ShellServiceImpl
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.Configurator
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.config.*
import cz.fit.metacentrum.service.input.CommandLineParser
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.service.input.validator.ConfigValidationService
import cz.fit.metacentrum.service.input.validator.IterationConfigValidator
import cz.fit.metacentrum.service.list.*
import cz.fit.metacentrum.service.submit.ActionSubmitService
import cz.fit.metacentrum.service.submit.executor.*


class MainModule : AbstractModule() {

    override fun configure() {
        bind(MainService::class.java)

        // input parsing and serializations
        bind(SerializationService::class.java)
        bind(CommandLineParser::class.java)

        // validator
        bind(ConfigValidationService::class.java)
        bind(IterationConfigValidator::class.java)

        // action services
        bind(object : TypeLiteral<ActionService<ActionSubmit>>() {}).to(ActionSubmitService::class.java)
        bind(object : TypeLiteral<ActionService<ActionList>>() {}).to(ActionListService::class.java)

        // Shell service binding
        if (ProfileConfiguration.isDev()) {
            val dockerShellServiceProxy = ShellServiceDockerProxy(ShellServiceImpl())
            bind(ShellService::class.java).toInstance(dockerShellServiceProxy)
        } else {
            bind(ShellService::class.java).to(ShellServiceImpl::class.java)
        }
        bind(ConsoleReader::class.java)

        bindConfiguratorClasses()
        // bind action features
        bindSubmitActionClasses()
        bindListActionClasses()
        bindReSubmitActionClasses()
    }

    private fun bindConfiguratorClasses() {
        val binder = Multibinder.newSetBinder(binder(), Configurator::class.java)

        binder.addBinding().to(TaskNameConfigurator::class.java)
        binder.addBinding().to(ModulesConfigurator::class.java)
        binder.addBinding().to(ToolboxConfigurator::class.java)

        if (ProfileConfiguration.isDev()) {
            binder.addBinding().to(DevelopmentCleanerConfigurator::class.java)
        }
        bind(ConfiguratorRunnerService::class.java)
    }

    private fun bindReSubmitActionClasses() {
        bind(TaskResubmitService::class.java)
    }

    private fun bindListActionClasses() {
        bind(FailedJobFinderService::class.java)
        bind(MetadataInfoPrinter::class.java)

        bind(CheckQueueExecutor::class.java)
    }

    private fun bindSubmitActionClasses() {
        // matlab executors
        val matlabBinder = Multibinder.newSetBinder(binder(), TaskExecutor::class.java)
        matlabBinder.addBinding().to(ResolvePathExecutor::class.java)
        matlabBinder.addBinding().to(UsernameResolverExecutor::class.java)
        matlabBinder.addBinding().to(InitOutputPathExecutor::class.java)
        matlabBinder.addBinding().to(CopySourcesFilesExecutor::class.java)
        matlabBinder.addBinding().to(IterationExecutor::class.java)
        matlabBinder.addBinding().to(MatlabScriptsExecutor::class.java)
        matlabBinder.addBinding().to(SubmitExecutor::class.java)

        bind(MatlabTemplateDataBuilder::class.java)

    }
}