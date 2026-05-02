package core.yggdrasil.data

import core.yggdrasil.storage.AppDirs
import core.yggdrasil.storage.FileScanner
import core.yggdrasil.viewmodels.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ItemRepository {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    fun addItem(title: String, content: String) {
        // Prevent duplicates by checking if content already exists
        if (_items.value.any { it.content == content }) return
        
        val newItem = Item(_items.value.size + 1, title, content)
        _items.update { it + newItem }
    }

    fun deleteItem(itemId: Int) {
        _items.update { currentItems -> currentItems.filter { it.id != itemId } }
    }

    fun scanStorage(appDirs: AppDirs) {
        val scannedFiles = FileScanner.scanMedia(appDirs.allStorageDirs)
        scannedFiles.forEach { file ->
            addItem(file.name, "Location: ${file.path}")
        }
    }
    
    fun clear() {
        _items.value = emptyList()
    }
}