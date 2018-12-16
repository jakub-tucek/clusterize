package cz.fit.metacentrum.config

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import com.google.inject.name.Names
import cz.fit.metacentrum.domain.*
import cz.fit.metacentrum.service.*
import cz.fit.metacentrum.service.action.daemon.*
import cz.fit.metacentrum.service.action.list.*
import cz.fit.metacentrum.service.action.submit.ActionResubmitFailedService
import cz.fit.metacentrum.service.action.submit.ActionSubmitService
import cz.fit.metacentrum.service.action.submit.executor.*
import cz.fit.metacentrum.service.action.submit.executor.re.CleanFailedJobDirectoryExecutor
import cz.fit.metacentrum.service.action.submit.executor.re.CleanStateExecutor
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.Configurator
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.config.*
import cz.fit.metacentrum.service.input.CommandLineParser
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.service.input.validator.ConfigValidationService
import cz.fit.metacentrum.service.input.validator.IterationConfigValidator


const val matlabExecutorsToken = "MATLAB_EXECUTORS_TOKEN"
const val matlabResubmitExecutorsToken = "MATLAB_RESUBMIT_EXECUTORS_TOKEN"

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
        bind(object : TypeLiteral<ActionService<ActionStatus>>() {}).to(ActionStatusService::class.java)
        bind(object : TypeLiteral<ActionService<ActionResubmitFailed>>() {}).to(ActionResubmitFailedService::class.java)
        bind(object : TypeLiteral<ActionService<ActionDaemon>>() {}).to(ActionDaemonService::class.java)
        bind(object : TypeLiteral<ActionService<ActionDaemonStartInternal>>() {}).to(ActionDaemonStartInternalService::class.java)

        // Shell service binding
        if (ProfileConfiguration.isDev()) {
            val dockerShellServiceProxy = ShellServiceDockerProxy(ShellServiceImpl())
            bind(ShellService::class.java).toInstance(dockerShellServiceProxy)
        } else {
            bind(ShellService::class.java).to(ShellServiceImpl::class.java)
        }
        bind(ConsoleReader::class.java)
        bind(SubmitRunner::class.java)
        bind(TemplateService::class.java)

        bindConfiguratorClasses()
        // bind action features
        bindSubmitActionClasses()
        bindListActionClasses()
        bindResubmitActionClasses()
        bindDaemonClasses()
    }

    private fun bindDaemonClasses() {
        bind(DaemonService::class.java)
        bind(CronService::class.java)
        bind(WatcherService::class.java)
    }

    private fun bindConfiguratorClasses() {
        val binder = Multibinder.newSetBinder(binder(), Configurator::class.java)

        binder.addBinding().to(TaskNameConfigurator::class.java)
        binder.addBinding().to(ModulesConfigurator::class.java)
        binder.addBinding().to(ToolboxConfigurator::class.java)
        binder.addBinding().to(ResourcesConfigurator::class.java)

        if (ProfileConfiguration.isDev()) {
            binder.addBinding().to(DevelopmentCleanerConfigurator::class.java)
        }
        bind(ConfiguratorRunner::class.java)
    }

    private fun bindResubmitActionClasses() {
        bind(TaskResubmitService::class.java)
        bind(ActionResubmitFailedService::class.java)

        val resubmitBinder = Multibinder.newSetBinder(
                binder(),
                TaskExecutor::class.java,
                Names.named(matlabResubmitExecutorsToken)
        )
        resubmitBinder.addBinding().to(CleanFailedJobDirectoryExecutor::class.java)
        resubmitBinder.addBinding().to(SubmitExecutor::class.java)
        resubmitBinder.addBinding().to(CleanStateExecutor::class.java)

        bind(MatlabTemplateDataBuilder::class.java)
    }

    private fun bindListActionClasses() {
        bind(FailedJobFinderService::class.java)
        bind(MetadataInfoPrinter::class.java)
        bind(CheckQueueExecutor::class.java)

        bind(MetadataStatusService::class.java)
    }

    private fun bindSubmitActionClasses() {
        // matlab executors
        val matlabBinder = Multibinder.newSetBinder(
                binder(),
                TaskExecutor::class.java,
                Names.named(matlabExecutorsToken)
        )
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