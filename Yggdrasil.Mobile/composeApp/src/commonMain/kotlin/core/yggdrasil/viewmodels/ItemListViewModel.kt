package core.yggdrasil.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

@Serializable
data class Item(val id: Int, val title: String, val content: String)

class ItemListViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _items = MutableStateFlow(
        savedStateHandle.get<List<Item>>("items") ?: emptyList()
    )
    val items = _items.asStateFlow()

    fun addItem(title: String, content: String) {
        val newItem = Item(_items.value.size + 1, title, content)
        _items.update { it + newItem }
        savedStateHandle["items"] = _items.value
    }

    fun deleteItem(itemId: Int) {
        _items.update { currentItems -> currentItems.filter { it.id != itemId } }
        savedStateHandle["items"] = _items.value
    }
}