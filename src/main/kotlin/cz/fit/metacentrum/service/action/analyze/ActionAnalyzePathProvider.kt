package cz.fit.metacentrum.service.action.analyze

import cz.fit.metacentrum.config.FileNames
import java.nio.file.Path
import java.nio.file.Paths

/**
 *
 * @author Jakub Tucek
 */
class ActionAnalyzePathProvider {


    fun retrieveAnalysisPath(): Path = Paths.get(FileNames.analysisFile)

    fun retrieveSpecificAnalysisPath(): Path = Paths.get(FileNames.specificAnalysisFile)

}