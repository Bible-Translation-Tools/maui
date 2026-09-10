package org.bibletranslationtools.maui.jvm.di.modules

import dagger.Binds
import dagger.Module
import org.bibletranslationtools.maui.common.persistence.IBatchRepository
import org.bibletranslationtools.maui.common.persistence.IPrefRepository
import org.bibletranslationtools.maui.jvm.persistence.BatchRepository
import org.bibletranslationtools.maui.jvm.persistence.PrefRepository
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideBatchRepository(repository: BatchRepository) : IBatchRepository

    @Binds
    @Singleton
    abstract fun providePrefRepository(repository: PrefRepository) : IPrefRepository
}