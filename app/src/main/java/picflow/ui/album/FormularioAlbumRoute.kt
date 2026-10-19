package picflow.ui.album

import android.content.ActivityNotFoundException
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import picflow.viewmodel.AlbumViewModel

/** Conecta el formulario con el ViewModel y con la cámara (recurso nativo). */
@Composable
fun FormularioAlbumRoute(
    viewModel: AlbumViewModel,
    onGuardado: (Int) -> Unit
) {
    val context = LocalContext.current

    // Ruta del archivo donde la cámara va a guardar la foto (sobrevive a rotaciones)
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        val ruta = rutaPendiente
        if (ruta != null) {
            if (tomada) viewModel.actualizarPortada(ruta) else File(ruta).delete()
        }
        rutaPendiente = null
    }

    FormularioAlbumScreen(
        state = viewModel.uiState,
        onNombreChange = viewModel::actualizarNombre,
        onDescripcionChange = viewModel::actualizarDescripcion,
        onTipoChange = viewModel::actualizarTipo,
        onPrivadoChange = viewModel::actualizarPrivado,
        onTomarFoto = {
            val archivo = crearArchivoPortada(context)
            val uri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", archivo
            )
            rutaPendiente = archivo.absolutePath
            try {
                camara.launch(uri)
                //Ignorar aun no se implementa uso en rama
            } catch (e: ActivityNotFoundException) {
                rutaPendiente = null
                archivo.delete()
                Toast.makeText(context, "No hay una app de cámara disponible", Toast.LENGTH_SHORT).show()
            }
        },
        onQuitarFoto = { viewModel.actualizarPortada(null) },
        onGuardar = { viewModel.guardar(onGuardado) }
    )
}

private fun crearArchivoPortada(context: Context): File {
    val carpeta = File(context.filesDir, "portadas").apply { mkdirs() }
    return File(carpeta, "portada_${System.currentTimeMillis()}.jpg")
}
