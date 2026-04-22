package core.yggdrasil.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import core.yggdrasil.content.AppButton
import org.jetbrains.compose.resources.painterResource
import yggdrasil.composeapp.generated.resources.Res
import yggdrasil.composeapp.generated.resources.yggdrasil

@Composable
fun ProfileTab(clickCount: Int, onClickCount: () -> Unit) {
    Column {
        Text("Profile details (Clicks: $clickCount)")
        AppButton(onClick = onClickCount, painter = painterResource(Res.drawable.yggdrasil))
    }
}