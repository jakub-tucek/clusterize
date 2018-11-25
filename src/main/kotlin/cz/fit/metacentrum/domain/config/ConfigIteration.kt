package cz.fit.metacentrum.domain.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName


const val ConfigIterationArrayType = "ARRAY"
const val ConfigIterationIntRangeType = "INT_RANGE"
const val ConfigIterationDependentType = "DEPENDENT"

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = ConfigIterationArray::class, name = ConfigIterationArrayType),
        JsonSubTypes.Type(value = ConfigIterationIntRange::class, name = ConfigIterationIntRangeType),
        JsonSubTypes.Type(value = ConfigIterationDependent::class, name = ConfigIterationDependentType)
)
sealed class ConfigIteration(open val name: String)

@JsonTypeName(ConfigIterationArrayType)
data class ConfigIterationArray(val values: List<String>,
                                override val name: String) : ConfigIteration(name)

@JsonTypeName(ConfigIterationIntRangeType)
data class ConfigIterationIntRange(val from: Int,
                                   val to: Int,
                                   override val name: String) : ConfigIteration(name)


@JsonTypeName(ConfigIterationDependentType)
data class ConfigIterationDependent(val dependentVarName: String,
                                    val modifier: String,
                                    override val name: String) : ConfigIteration(name)
