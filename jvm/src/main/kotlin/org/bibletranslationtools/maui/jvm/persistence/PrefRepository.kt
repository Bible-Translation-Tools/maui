package org.bibletranslationtools.maui.jvm.persistence

import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.bibletranslationtools.maui.common.persistence.IDirectoryProvider
import org.bibletranslationtools.maui.common.persistence.IPrefRepository
import javax.inject.Inject

class PrefRepository @Inject constructor(directoryProvider: IDirectoryProvider) : IPrefRepository {
    private val builder: FileBasedConfigurationBuilder<PropertiesConfiguration>
    private val config: PropertiesConfiguration

    init {
        val configs = Configurations()
        val propertiesFile = directoryProvider.prefFile

        // Create empty properties file if it doesn't exist
        if(!propertiesFile.exists()) {
            propertiesFile.createNewFile()
        }

        builder = configs.propertiesBuilder(propertiesFile)
        config = builder.configuration
    }

    override fun get(key: String, default: String): String {
        return config.getString(key, default)
    }

    override fun put(key: String, value: String) {
        config.setProperty(key, value)
        builder.save()
    }
}