package core.yggdrasil.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import core.yggdrasil.viewmodels.DownloadState
import core.yggdrasil.viewmodels.DownloadViewModel
import core.yggdrasil.ui.Dimensions
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadTab() {
    val viewModel: DownloadViewModel = koinViewModel()
    val state by viewModel.downloadState.collectAsState()
    
    var url by remember { mutableStateOf("https://raw.githubusercontent.com/Kotlin/kotlinx-io/master/README.md") }
    var fileName by remember { mutableStateOf("readme.md") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.Padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        Text("File Downloader", style = MaterialTheme.typography.headlineMedium)
        
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Download URL") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = fileName,
            onValueChange = { fileName = it },
            label = { Text("File Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { viewModel.startDownload(url, fileName) },
            enabled = state !is DownloadState.Downloading && url.isNotBlank() && fileName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Download")
        }
        
        when (val currentState = state) {
            is DownloadState.Downloading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { currentState.progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("${(currentState.progress * 100).toInt()}%")
                }
            }
            is DownloadState.Completed -> {
                Text("Download Completed!", color = MaterialTheme.colorScheme.primary)
                Text("Saved to: ${currentState.path}", style = MaterialTheme.typography.bodySmall)
            }
            is DownloadState.Error -> {
                Text("Error: ${currentState.message}", color = MaterialTheme.colorScheme.error)
            }
            DownloadState.Idle -> {
                Text("Ready to download")
            }
        }
    }
}
