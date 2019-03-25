package cz.fit.metacentrum.domain.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName


private const val ConfigIterationArrayType = "ARRAY"
private const val ConfigIterationIntRangeType = "INT_RANGE"

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = ConfigIterationArray::class, name = ConfigIterationArrayType),
        JsonSubTypes.Type(value = ConfigIterationIntRange::class, name = ConfigIterationIntRangeType)
)
sealed class ConfigIteration(open val name: String)

@JsonTypeName(ConfigIterationArrayType)
data class ConfigIterationArray(val values: List<String>,
                                override val name: String) : ConfigIteration(name)

@JsonTypeName(ConfigIterationIntRangeType)
data class ConfigIterationIntRange(val from: Int,
                                   val to: Int,
                                   val step: Int = 1,
                                   val stepOperation: StepOperation = StepOperation.PLUS,
                                   override val name: String) : ConfigIteration(name)
