package picflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import picflow.data.AlbumRepository

/** Le entrega el repositorio al ViewModel (ya no tiene UN constructor vacío). */
class AlbumViewModelFactory(private val repository: AlbumRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AlbumViewModel(repository) as T
}
