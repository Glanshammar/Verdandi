package core.yggdrasil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import yggdrasil.composeapp.generated.resources.Res
import yggdrasil.composeapp.generated.resources.*
import core.yggdrasil.content.*

@Composable
@Preview
fun App() {
    var clickCount by remember { mutableStateOf(0) }

    val tabs = listOf(
        Tab(
            title = "Home",
            icon = painterResource(Res.drawable.house),
            content = { HomeTab() }
        ),
        Tab(
            title = "Profile",
            icon = painterResource(Res.drawable.user),
            content = { ProfileTab(clickCount) { clickCount++ } }
        ),
        Tab(
            title = "Settings",
            icon = painterResource(Res.drawable.settings),
            content = { SettingsTab() }
        )
    )

    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AppButton(
                onClick = { clickCount++ },
                painter = painterResource(Res.drawable.yggdrasil),
                size = 64.dp
            )

            Text(
                text = "Clicks: $clickCount",
                style = MaterialTheme.typography.bodyLarge
            )

            TabsComponent(tabs=tabs)
        }
    }
}