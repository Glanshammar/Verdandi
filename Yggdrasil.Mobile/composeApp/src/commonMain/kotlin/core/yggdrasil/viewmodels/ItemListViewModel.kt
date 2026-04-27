package core.yggdrasil.viewmodels

import androidx.lifecycle.ViewModel
import core.yggdrasil.data.ItemRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class Item(val id: Int, val title: String, val content: String)

class ItemListViewModel : ViewModel() {
    val items: StateFlow<List<Item>> = ItemRepository.items

    fun addItem(title: String, content: String) {
        ItemRepository.addItem(title, content)
    }

    fun deleteItem(itemId: Int) {
        ItemRepository.deleteItem(itemId)
    }
}