package core.yggdrasil.di

import core.yggdrasil.data.ItemRepository
import core.yggdrasil.network.FileDownloader
import core.yggdrasil.network.NetworkClient
import core.yggdrasil.viewmodels.DownloadViewModel
import core.yggdrasil.viewmodels.ItemListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { NetworkClient.client }
    single { FileDownloader(get()) }
    single { ItemRepository }
    
    viewModelOf(::ItemListViewModel)
    viewModelOf(::DownloadViewModel)
}

expect fun platformModule(): Module
