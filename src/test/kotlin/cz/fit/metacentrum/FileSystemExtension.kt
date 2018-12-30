package cz.fit.metacentrum

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Re-initializes TestData after each test
 * @author Jakub Tucek
 */
class FileSystemExtension : AfterEachCallback {
    override fun afterEach(context: ExtensionContext) {
        TestData.initProperties()
    }
}