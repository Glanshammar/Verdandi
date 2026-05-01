package core.yggdrasil.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun FilePicker(onFileSelected: (String?) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        onFileSelected(uri?.toString())
    }

    return remember {
        {
            launcher.launch(arrayOf("video/*", "audio/*"))
        }
    }
}
