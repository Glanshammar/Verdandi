package core.yggdrasil.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                modifier = Modifier.padding(Dimensions.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
            ) {
                Text("Available Endpoints", style = MaterialTheme.typography.titleMedium)
                
                Button(
                    onClick = { viewModel.callOnlineStatus() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GET /status (onlineStatus)")
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
                        when (val data = currentState.data) {
                            is ApiStatus -> {
                                Column {
                                    Text("Status: ${data.status ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Message: ${data.message ?: "N/A"}")
                                    Text("Timestamp: ${data.timestamp ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            is List<*> -> {
                                LazyColumn {
                                    items(data) { item ->
                                        if (item is Item) {
                                            Text("${item.title}: ${item.content}")
                                            HorizontalDivider()
                                        } else {
                                            Text(item.toString())
                                        }
                                    }
                                }
                            }
                            else -> Text(data.toString())
                        }
                    }
                }
            }
        }
    }
}
