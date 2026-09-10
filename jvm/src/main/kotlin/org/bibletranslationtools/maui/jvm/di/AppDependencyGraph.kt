/**
 * Copyright (C) 2020-2024 Wycliffe Associates
 *
 * This file is part of Orature.
 *
 * Orature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Orature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Orature.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.bibletranslationtools.maui.jvm.di

import dagger.Component
import org.bibletranslationtools.maui.jvm.MainApp
import org.bibletranslationtools.maui.jvm.di.modules.DirectoryProviderModule
import org.bibletranslationtools.maui.jvm.di.modules.ReadersModule
import org.bibletranslationtools.maui.jvm.di.modules.RepositoryModule
import org.bibletranslationtools.maui.jvm.di.modules.SoxBinaryProviderModule
import org.bibletranslationtools.maui.jvm.ui.BatchDataStore
import org.bibletranslationtools.maui.jvm.ui.ImportFilesViewModel
import org.bibletranslationtools.maui.jvm.ui.batch.BatchViewModel
import org.bibletranslationtools.maui.jvm.ui.upload.UploadMediaViewModel
import javax.inject.Singleton

@Component(
    modules = [
        DirectoryProviderModule::class,
        RepositoryModule::class,
        ReadersModule::class,
        SoxBinaryProviderModule::class
    ]
)

@Singleton
interface AppDependencyGraph {
    fun inject(app: MainApp)
    fun inject(viewModel: BatchViewModel)
    fun inject(viewModel: UploadMediaViewModel)
    fun inject(viewModel: ImportFilesViewModel)
    fun inject(dataStore: BatchDataStore)
}