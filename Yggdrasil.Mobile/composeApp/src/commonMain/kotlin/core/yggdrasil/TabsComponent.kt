package core.yggdrasil

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

data class Tab(
    val title: String? = null,
    val icon: Painter?,
    val content: @Composable () -> Unit
)

@Composable
fun TabsComponent(
    tabs: List<Tab>,
    selectedTabIndex: Int = 0
) {
    var currentTabIndex by remember { mutableStateOf(selectedTabIndex) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = currentTabIndex) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = currentTabIndex == index,
                    onClick = { currentTabIndex = index },
                    text = { if (tab.title != null) Text(tab.title) },
                    icon = {
                        tab.icon?.let { painter ->
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        }
        tabs[currentTabIndex].content()
    }
}