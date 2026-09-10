package org.bibletranslationtools.maui.jvm.persistence

import io.mockk.every
import io.mockk.mockk
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.junit.Assert
import org.junit.Test
import kotlin.io.path.createTempFile as ktCreateTempFile

class PrefRepositoryTest {

    private val directoryProvider = mockk<IDirectoryProvider> {
        every { prefFile } returns ktCreateTempFile("pref",".properties").toFile().apply {
            deleteOnExit()
        }
    }

    @Test
    fun putAndGetTest() {
        val repo = PrefRepository(directoryProvider)

        repo.put("test", "example")

        Assert.assertEquals("example", repo.get("test"))
    }
}