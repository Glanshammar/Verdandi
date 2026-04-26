package core.yggdrasil

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import core.yggdrasil.content.*
import core.yggdrasil.views.*

@Composable
@Preview
fun App() {
    var clickCount by remember { mutableStateOf(0) }

    MaterialTheme {
        SidebarNavDrawer { selectedItem ->
            when (selectedItem) {
                NavigationItem.Home -> HomeTab()
                NavigationItem.Profile -> ProfileTab(
                    clickCount = clickCount,
                    onClickCount = { clickCount++ }
                )
                NavigationItem.Settings -> SettingsTab()
            }
        }
    }
}
