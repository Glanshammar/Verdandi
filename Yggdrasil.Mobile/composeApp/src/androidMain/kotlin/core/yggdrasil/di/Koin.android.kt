package core.yggdrasil.di

import core.yggdrasil.storage.AndroidAppDirs
import core.yggdrasil.storage.AppDirs
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDirs> { AndroidAppDirs(get()) }
}
