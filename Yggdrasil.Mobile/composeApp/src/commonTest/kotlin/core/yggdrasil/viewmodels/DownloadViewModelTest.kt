package core.yggdrasil.viewmodels

import core.yggdrasil.data.ItemRepository
import core.yggdrasil.network.FileDownloader
import core.yggdrasil.network.NetworkClient
import core.yggdrasil.storage.AppDirs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.io.files.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeAppDirs : AppDirs {
    override val downloadsDir: Path = Path("/tmp/downloads")
    override val cacheDir: Path = Path("/tmp/cache")
}

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        ItemRepository.clear()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateIsIdle() {
        val viewModel = DownloadViewModel(FakeAppDirs(), FileDownloader(NetworkClient.client))
        assertEquals(DownloadState.Idle, viewModel.downloadState.value)
    }
}
