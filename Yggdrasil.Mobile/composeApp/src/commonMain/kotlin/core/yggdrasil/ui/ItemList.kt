package core.yggdrasil.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import core.yggdrasil.viewmodels.ItemListViewModel
import org.jetbrains.compose.resources.painterResource
import yggdrasil.composeapp.generated.resources.Res
import yggdrasil.composeapp.generated.resources.yggdrasil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemList(viewModel: ItemListViewModel = viewModel()) {
    val items by viewModel.items.collectAsState()

    LazyColumn {
        items(items, key = { it.id }) { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                supportingContent = { Text(item.content) },
                leadingContent = {
                    Image(
                        painterResource(Res.drawable.yggdrasil),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp, 80.dp)
                    )
                },
                modifier = Modifier.clickable {
                    viewModel.deleteItem(item.id)
                }
            )
        }
    }
}