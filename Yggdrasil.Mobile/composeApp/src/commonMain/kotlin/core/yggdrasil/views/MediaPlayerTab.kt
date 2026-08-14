package core.yggdrasil.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    val mediaItems by viewModel.availableMedia.collectAsState()
    
    var inputUrl by remember { mutableStateOf("") }

    val filePicker = FilePicker { path ->
        path?.let { 
            viewModel.setLocalPath(it) 
            val fileName = it.substringAfterLast("/")
            ItemRepository.addItem(fileName, "Location: $it")
        }
    }

    val playerHost = remember { 
        MediaPlayerHost(
            mediaUrl = mediaUrl,
            autoPlay = true
        ) 
    }
    
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
            Text("Browse System Files")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Available Media Items", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { 
                viewModel.refreshRemoteMedia() 
                viewModel.scanLocalMedia()
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh media items")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mediaItems, key = { it.id }) { item ->
                Card(
                    onClick = { viewModel.setMedia(item.url) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = { Text("${item.type} - ${item.url}") },
                        trailingContent = {
                            if (item.isDownloaded) {
                                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                    Text("Downloaded")
                                }
                            }
                        },
                        colors = if (mediaUrl == item.url) {
                            ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else {
                            ListItemDefaults.colors()
                        }
                    )
                }
            }
            
            if (mediaItems.isEmpty()) {
                item {
                    Text(
                        "No media items found. Use the Downloads tab to add some or refresh the list.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = Dimensions.Padding)
                    )
                }
            }
        }
    }
}
