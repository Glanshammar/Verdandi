package core.yggdrasil.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.yggdrasil.network.api.ApiFile
import core.yggdrasil.network.api.ApiStatus
import core.yggdrasil.ui.Dimensions
import core.yggdrasil.viewmodels.ApiState
import core.yggdrasil.viewmodels.ApiViewModel
import core.yggdrasil.viewmodels.Item
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ApiTab() {
    val viewModel: ApiViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    var filePathInput by remember { mutableStateOf("test/file.txt") }
    var idInput by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.Padding),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        Text("API Console", style = MaterialTheme.typography.headlineMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimensions.SpacingMedium)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
            ) {
                Text("Available Endpoints", style = MaterialTheme.typography.titleMedium)
                
                Button(
                    onClick = { viewModel.callOnlineStatus() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GET /status (onlineStatus)")
                }

                Button(
                    onClick = { viewModel.callListFiles() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GET /files (listFiles)")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                OutlinedTextField(
                    value = filePathInput,
                    onValueChange = { filePathInput = it },
                    label = { Text("File Path") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.callAddFile(filePathInput) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = filePathInput.isNotBlank()
                ) {
                    Text("POST /files (addFile)")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                OutlinedTextField(
                    value = idInput,
                    onValueChange = { idInput = it },
                    label = { Text("File ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { idInput.toIntOrNull()?.let { viewModel.callGetFile(it) } },
                        modifier = Modifier.weight(1f),
                        enabled = idInput.isNotBlank() && idInput.toIntOrNull() != null
                    ) {
                        Text("GET")
                    }
                    Button(
                        onClick = { idInput.toIntOrNull()?.let { viewModel.callDeleteFile(it) } },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = idInput.isNotBlank() && idInput.toIntOrNull() != null
                    ) {
                        Text("DELETE")
                    }
                }
            }
        }

        Text("Response", style = MaterialTheme.typography.titleMedium)
        
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimensions.SpacingSmall)
            ) {
                when (val currentState = state) {
                    ApiState.Idle -> Text("Select an API call above")
                    ApiState.Loading -> CircularProgressIndicator()
                    is ApiState.Error -> Text("Error: ${currentState.message}", color = MaterialTheme.colorScheme.error)
                    is ApiState.Success -> {
                        SelectionContainer {
                            Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                when (val data = currentState.data) {
                                    is ApiStatus -> StatusView(data)
                                    is List<*> -> ListView(data)
                                    is ApiFile -> FileDetailView(data)
                                    else -> Text(data.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusView(data: ApiStatus) {
    Column {
        Text("Status: ${data.status ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
        Text("Message: ${data.message ?: "N/A"}")
        Text("Timestamp: ${data.timestamp ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ListView(items: List<*>) {
    Column {
        items.forEach { item ->
            when (item) {
                is ApiFile -> FileDetailView(item)
                is Item -> Text("${item.title}: ${item.content}")
                else -> Text(item.toString())
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun FileDetailView(file: ApiFile) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("ID: ${file.id}", style = MaterialTheme.typography.labelMedium)
        Text("Name: ${file.name}", style = MaterialTheme.typography.bodyLarge)
        Text("Path: ${file.filePath}", style = MaterialTheme.typography.bodyMedium)
        file.fileType?.let { Text("Type: $it", style = MaterialTheme.typography.bodySmall) }
        file.timeCreated?.let { Text("Created: $it", style = MaterialTheme.typography.labelSmall) }
    }
}
