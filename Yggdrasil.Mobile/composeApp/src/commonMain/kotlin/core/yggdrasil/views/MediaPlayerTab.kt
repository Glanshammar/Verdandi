package core.yggdrasil.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.yggdrasil.data.ItemRepository
import core.yggdrasil.ui.Dimensions
import core.yggdrasil.ui.FilePicker
import core.yggdrasil.viewmodels.MediaPlayerViewModel
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import chaintech.videoplayer.model.VideoPlayerConfig
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MediaPlayerTab() {
    val viewModel: MediaPlayerViewModel = koinViewModel()
    val mediaUrl by viewModel.mediaUrl.collectAsState()
    val items by ItemRepository.items.collectAsState()
    
    var inputUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }

    val filePicker = FilePicker { path ->
        path?.let { viewModel.setLocalPath(it) }
    }

    val playerHost = remember { 
        MediaPlayerHost(
            mediaUrl = mediaUrl,
            autoPlay = true
        ) 
    }
    
    // Update playerHost when mediaUrl changes in ViewModel
    LaunchedEffect(mediaUrl) {
        if (mediaUrl.isNotBlank()) {
            playerHost.loadUrl(mediaUrl)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.Padding),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        Text("Media Player", style = MaterialTheme.typography.headlineMedium)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            VideoPlayerComposable(
                playerHost = playerHost,
                modifier = Modifier.fillMaxSize(),
                playerConfig = VideoPlayerConfig(
                    isSeekBarVisible = true,
                    isDurationVisible = true,
                    isAutoHideControlEnabled = true,
                    showControls = true
                )
            )
        }

        OutlinedTextField(
            value = inputUrl,
            onValueChange = { inputUrl = it },
            label = { Text("Media URL") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { viewModel.setMedia(inputUrl) }) {
                    Text("Play")
                }
            }
        )

        Button(
            onClick = { filePicker() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Browse System Files (SD Card, etc.)")
        }

        Text("Recent / Downloaded Items", style = MaterialTheme.typography.titleMedium)
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    onClick = { 
                        val path = item.content.removePrefix("Location: ")
                        viewModel.setLocalPath(path) 
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = { Text(item.content) },
                        colors = if (mediaUrl == "file://${item.content.removePrefix("Location: ")}" || mediaUrl == item.content.removePrefix("Location: ")) {
                            ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else {
                            ListItemDefaults.colors()
                        }
                    )
                }
            }
            
            if (items.isEmpty()) {
                item {
                    Text(
                        "No downloaded items found. Use the Downloads tab to add some!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}
