package cz.fit.metacentrum.ext

import cz.fit.metacentrum.domain.management.QueueDataSource
import cz.fit.metacentrum.domain.management.QueueInformation


interface DataSourceProvider {

    fun parseDatasource(dataSource: QueueDataSource): List<QueueInformation>

}
