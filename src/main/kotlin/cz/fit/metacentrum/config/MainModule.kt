package cz.fit.metacentrum.config

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import com.google.inject.name.Names
import cz.fit.metacentrum.domain.*
import cz.fit.metacentrum.service.*
import cz.fit.metacentrum.service.action.cron.ActionCronService
import cz.fit.metacentrum.service.action.cron.ActionCronStartInternalService
import cz.fit.metacentrum.service.action.cron.CronService
import cz.fit.metacentrum.service.action.cron.WatcherService
import cz.fit.metacentrum.service.action.resubmit.ActionResubmitService
import cz.fit.metacentrum.service.action.resubmit.CleanEmptyStateFoldersExecutor
import cz.fit.metacentrum.service.action.status.ActionStatusService
import cz.fit.metacentrum.service.action.status.MetadataInfoPrinter
import cz.fit.metacentrum.service.action.status.MetadataStatusService
import cz.fit.metacentrum.service.action.status.ex.CheckQueueExecutor
import cz.fit.metacentrum.service.action.status.ex.JobStatusCheckExecutor
import cz.fit.metacentrum.service.action.status.ex.ReadJobInfoFileExecutor
import cz.fit.metacentrum.service.action.status.ex.UpdateMetadataStateExecutor
import cz.fit.metacentrum.service.action.submit.ActionSubmitService
import cz.fit.metacentrum.service.action.submit.executor.*
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.Configurator
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.config.*
import cz.fit.metacentrum.service.input.CommandLineParser
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.service.input.validator.ConfigValidationService
import cz.fit.metacentrum.service.input.validator.IterationConfigValidator


const val actionSubmitMatlabExecutorsToken = "ACTION_SUBMIT_MATLAB_EXECUTORS"
const val actionResubmitMatlabExecutorsTokens = "ACTION_RESUBMIT_MATLAB_EXECUTORS"
const val actionStatusExecutorsToken = "ACTION_STATUS_EXECUTORS"

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
        bind(object : TypeLiteral<ActionService<ActionResubmit>>() {}).to(ActionResubmitService::class.java)
        bind(object : TypeLiteral<ActionService<ActionCron>>() {}).to(ActionCronService::class.java)
        bind(object : TypeLiteral<ActionService<ActionCronStartInternal>>() {}).to(ActionCronStartInternalService::class.java)

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
        bindStatusActionClasses()
        bindResubmitActionClasses()
        bindCronClasses()
    }

    private fun bindCronClasses() {
        bind(CronService::class.java)
        bind(WatcherService::class.java)
    }

    private fun bindConfiguratorClasses() {
        val binder = Multibinder.newSetBinder(binder(), Configurator::class.java)

        binder.addBinding().to(TaskNameConfigurator::class.java)
        binder.addBinding().to(ModulesConfigurator::class.java)
        binder.addBinding().to(ToolboxConfigurator::class.java)
        binder.addBinding().to(ResourcesConfigurator::class.java)

        // add resource cleaner for develop profile
        if (ProfileConfiguration.isDev()) {
            binder.addBinding().to(DevelopmentCleanerConfigurator::class.java)
        }
        bind(ConfiguratorRunner::class.java)
    }

    private fun bindResubmitActionClasses() {
        bind(TaskResubmitService::class.java)
        bind(ActionResubmitService::class.java)

        val resubmitBinder = Multibinder.newSetBinder(
                binder(),
                TaskExecutor::class.java,
                Names.named(actionResubmitMatlabExecutorsTokens)
        )
        resubmitBinder.addBinding().to(CleanEmptyStateFoldersExecutor::class.java)
        resubmitBinder.addBinding().to(SubmitExecutor::class.java)

        bind(MatlabTemplateDataBuilder::class.java)
    }

    private fun bindStatusActionClasses() {
        bind(MetadataInfoPrinter::class.java)

        val executors = Multibinder.newSetBinder(
                binder(),
                TaskExecutor::class.java,
                Names.named(actionStatusExecutorsToken)
        )
        executors.addBinding().to(ReadJobInfoFileExecutor::class.java)
        executors.addBinding().to(JobStatusCheckExecutor::class.java)
        executors.addBinding().to(CheckQueueExecutor::class.java)
        executors.addBinding().to(UpdateMetadataStateExecutor::class.java)


        bind(MetadataStatusService::class.java)
    }

    private fun bindSubmitActionClasses() {
        // matlab executors
        val matlabBinder = Multibinder.newSetBinder(
                binder(),
                TaskExecutor::class.java,
                Names.named(actionSubmitMatlabExecutorsToken)
        )
        matlabBinder.addBinding().to(ResolvePathExecutor::class.java)
        matlabBinder.addBinding().to(UsernameResolverExecutor::class.java)
        matlabBinder.addBinding().to(InitOutputPathExecutor::class.java)
        matlabBinder.addBinding().to(CopySourcesFilesExecutor::class.java)
        matlabBinder.addBinding().to(IterationExecutor::class.java)
        matlabBinder.addBinding().to(SaveMetadataIdPathMappingExecutor::class.java)
        matlabBinder.addBinding().to(MatlabScriptsExecutor::class.java)
        matlabBinder.addBinding().to(SubmitExecutor::class.java)
        matlabBinder.addBinding().to(UpdateMetadataStateExecutor::class.java)

        bind(MatlabTemplateDataBuilder::class.java)
    }
}