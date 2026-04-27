package core.yggdrasil

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import core.yggdrasil.ui.*
import core.yggdrasil.views.*

@Composable
@Preview
fun App() {
    var clickCount by remember { mutableStateOf(0) }

    val colorScheme = lightColorScheme(
        primary = AppColors.Primary,
        secondary = AppColors.Secondary,
        background = AppColors.Background,
        surface = AppColors.Surface,
        error = AppColors.Error,
        onPrimary = AppColors.OnPrimary,
        onSecondary = AppColors.OnSecondary,
        onBackground = AppColors.OnBackground,
        onSurface = AppColors.OnSurface,
        onError = AppColors.OnError
    )

    MaterialTheme(colorScheme = colorScheme) {
        SidebarNavDrawer { selectedItem ->
            when (selectedItem) {
                NavigationItem.Home -> HomeTab()
                NavigationItem.Profile -> ProfileTab(
                    clickCount = clickCount,
                    onClickCount = { clickCount++ }
                )
                NavigationItem.Downloads -> DownloadTab()
                NavigationItem.Settings -> SettingsTab()
            }
        }
    }
}
