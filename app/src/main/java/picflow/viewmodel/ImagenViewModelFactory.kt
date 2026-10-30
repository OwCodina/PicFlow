
package picflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import picflow.data.ImagenRepository

class ImagenViewModelFactory(private val repository: ImagenRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ImagenViewModel(repository) as T
}

class EditorViewModelFactory(private val repository: ImagenRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EditorViewModel(repository) as T
}
