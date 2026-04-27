package core.yggdrasil.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import core.yggdrasil.viewmodels.ItemListViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import yggdrasil.composeapp.generated.resources.Res
import yggdrasil.composeapp.generated.resources.yggdrasil

@Composable
fun ItemList(
    modifier: Modifier = Modifier,
    viewModel: ItemListViewModel = koinViewModel()
) {
    val items by viewModel.items.collectAsState()

    LazyColumn(modifier = modifier) {
        items(items, key = { it.id }) { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                supportingContent = { Text(item.content) },
                leadingContent = {
                    Image(
                        painterResource(Res.drawable.yggdrasil),
                        contentDescription = "Item icon",
                        modifier = Modifier.size(Dimensions.ListItemImageSize)
                    )
                },
                trailingContent = {
                    IconButton(onClick = { viewModel.deleteItem(item.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete item ${item.title}"
                        )
                    }
                }
            )
        }
    }
}
