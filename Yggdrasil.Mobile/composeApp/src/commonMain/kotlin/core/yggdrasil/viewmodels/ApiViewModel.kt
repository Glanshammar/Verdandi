package core.yggdrasil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.yggdrasil.data.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ApiState {
    data object Idle : ApiState()
    data object Loading : ApiState()
    data class Success(val data: Any) : ApiState()
    data class Error(val message: String) : ApiState()
}

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    private val _state = MutableStateFlow<ApiState>(ApiState.Idle)
    val state = _state.asStateFlow()

    fun callOnlineStatus() {
        viewModelScope.launch {
            _state.value = ApiState.Loading
            try {
                val result = repository.getOnlineStatus()
                _state.value = ApiState.Success(result)
            } catch (e: Exception) {
                _state.value = ApiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
