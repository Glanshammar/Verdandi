package core.yggdrasil.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        _items.value += newItem
        savedStateHandle["items"] = _items.value
    }

    fun deleteItem(itemId: Int) {
        _items.value = _items.value.filter { it.id != itemId }
        savedStateHandle["items"] = _items.value
    }
}