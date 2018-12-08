package cz.fit.metacentrum.service.input

import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.time.LocalDateTime


/**
 * @author Jakub Tucek
 */
internal class SerializationServiceTest {

    private val folderPath = "src/test/resources/configFileParser"

    private val serializationService = SerializationService()

    @Test
    fun parse() {
        val res = serializationService.parseConfig("$folderPath/configFileTest.yml")

        Assertions.assertThat(res.iterations.isEmpty()).isFalse()
        Assertions.assertThat(res.general.taskName).isEqualTo("matlab-dip")
        Assertions.assertThat(res.iterations[0].name).isEqualTo("VICINITY_TYPE")
        val task = res.taskType as MatlabTaskType
        Assertions.assertThat(task.functionCall)
                .isEqualTo("main_batch01(\$MIN_TRANSL, \$MAX_TRANSL, \$VICINITY_TYPE, \$SUB_IMG_IDX, 'useGPU', 'yes', 'layers', \$ADDED_LAYERS)")
    }

    @Test
    fun parseFail() {
        val throwsRes = assertThrows(IllegalArgumentException::class.java) {
            serializationService.parseConfig("$folderPath/configFileTestInvalid.yml")
        }

        Assertions.assertThat(throwsRes.message)
                .isNotBlank()
    }

    @Test
    fun testSerializationWhenMetadataExists() {
        val data = TestData.executedMetadata
        Files.createDirectories(data.paths.metadataStoragePath)
        // persist metadata
        serializationService.persistMetadata(data)
        val readMetadata = serializationService.parseMetadata(data.paths.metadataStoragePath!!)
        // check that read data are same
        Assertions.assertThat(readMetadata).isEqualTo(data)

        // update metadata and persist them
        val newData = data.copy(updateTime = LocalDateTime.now().plusDays(5))
        serializationService.persistMetadata(newData)
        val readNewData = serializationService.parseMetadata(newData.paths.metadataStoragePath!!)
        // check that reading data was succesful
        Assertions.assertThat(readNewData).isEqualTo(newData)
    }
}