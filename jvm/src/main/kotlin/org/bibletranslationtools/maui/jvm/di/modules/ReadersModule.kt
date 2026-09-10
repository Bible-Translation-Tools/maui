package org.bibletranslationtools.maui.jvm.di.modules

import dagger.Binds
import dagger.Module
import org.bibletranslationtools.maui.common.io.*
import org.bibletranslationtools.maui.jvm.io.*
import javax.inject.Singleton

@Module
abstract class ReadersModule {
    @Binds
    @Singleton
    abstract fun providesVersificationReader(reader: VersificationReader) : IVersificationReader

    @Binds
    @Singleton
    abstract fun providesBooksReader(reader: BooksReader) : IBooksReader

    @Binds
    @Singleton
    abstract fun providesLanguagesReader(reader: AllLanguagesReader) : ILanguagesReader

    @Binds
    @Singleton
    abstract fun providesResourceTypesReader(reader: ResourceTypesReader) : IResourceTypesReader
}