package core.yggdrasil.ui

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(onFileSelected: (String?) -> Unit): () -> Unit
