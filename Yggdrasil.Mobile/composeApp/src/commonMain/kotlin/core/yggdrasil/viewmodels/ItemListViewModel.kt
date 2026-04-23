package core.yggdrasil.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Item(val id: Int, val title: String, val content: String)

class ItemListViewModel : ViewModel() {
    private val _items = MutableStateFlow(emptyList<Item>())
    val items = _items.asStateFlow()

    fun addItem(title: String, content: String) {
        val newItem = Item(_items.value.size + 1, title, content)
        _items.value += newItem
    }

    fun deleteItem(itemId: Int) {
        _items.value = _items.value.filter { it.id != itemId }
    }
}