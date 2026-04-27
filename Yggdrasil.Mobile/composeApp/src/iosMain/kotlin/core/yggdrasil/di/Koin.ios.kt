package core.yggdrasil.di

import core.yggdrasil.storage.AppDirs
import core.yggdrasil.storage.IosAppDirs
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDirs> { IosAppDirs() }
}

fun initKoin() {
    startKoin {
        modules(appModule, platformModule())
    }
}
