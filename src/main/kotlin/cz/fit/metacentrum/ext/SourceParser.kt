package cz.fit.metacentrum.ext

import cz.fit.metacentrum.domain.management.QueueDataSource
import cz.fit.metacentrum.domain.management.QueueSource


interface SourceParser {

    fun parseDatasource(dataSource: QueueDataSource, sourceData: String): QueueSource

}
