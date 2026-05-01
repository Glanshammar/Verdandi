package core.yggdrasil.di

import core.yggdrasil.data.ItemRepository
import core.yggdrasil.data.ApiRepository
import core.yggdrasil.network.FileDownloader
import core.yggdrasil.network.NetworkClient
import core.yggdrasil.network.api.YggdrasilApi
import core.yggdrasil.viewmodels.ApiViewModel
import core.yggdrasil.viewmodels.DownloadViewModel
import core.yggdrasil.viewmodels.ItemListViewModel
import core.yggdrasil.viewmodels.MediaPlayerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { NetworkClient.client }
    single { FileDownloader(get()) }
    single { ItemRepository }
    single { YggdrasilApi(get()) }
    single { ApiRepository(get()) }

    viewModelOf(::ItemListViewModel)
    viewModelOf(::DownloadViewModel)
    viewModelOf(::MediaPlayerViewModel)
    viewModelOf(::ApiViewModel)
}

expect fun platformModule(): Module
