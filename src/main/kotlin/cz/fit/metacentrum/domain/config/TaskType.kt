package cz.fit.metacentrum.domain.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName


const val taskTypeMatlab = "MATLAB"
const val taskTypePython = "PYTHON"

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = MatlabTaskType::class, name = taskTypeMatlab),
        JsonSubTypes.Type(value = PythonTaskType::class, name = taskTypePython)
)
sealed class TaskType()

@JsonTypeName(taskTypeMatlab)
data class MatlabTaskType(
        val functionCall: String
) : TaskType()

@JsonTypeName(taskTypeMatlab)
data class PythonTaskType(
        val command: String
) : TaskType()