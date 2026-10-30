package picflow.ui.album

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import picflow.model.Album
import picflow.model.Imagen
import picflow.util.ArchivosFotos
import picflow.viewmodel.ImagenViewModel

/** Conecta el detalle del álbum con la cámara y la galería del teléfono (recursos nativos). */
@Composable
fun DetalleAlbumRoute(
    albumId: Int,
    album: Album?,
    imagenes: List<Imagen>,
    imagenViewModel: ImagenViewModel,
    mensaje: String?,
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: (Album) -> Unit,
    onAbrirImagen: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Archivo donde la cámara va a guardar la foto (sobrevive a rotaciones de pantalla)
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        val ruta = rutaPendiente
        if (ruta != null) {
            if (tomada) imagenViewModel.agregarFoto(albumId, ruta) else File(ruta).delete()
        }
        rutaPendiente = null
    }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                val ruta = withContext(Dispatchers.IO) { ArchivosFotos.copiarDesdeUri(context, uri) }
                if (ruta != null) {
                    imagenViewModel.agregarFoto(albumId, ruta)
                } else {
                    Toast.makeText(context, "No se pudo importar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DetalleAlbumScreen(
        album = album,
        imagenes = imagenes,
        mensaje = mensaje,
        onVolver = onVolver,
        onEditar = onEditar,
        onEliminar = onEliminar,
        onAbrirImagen = onAbrirImagen,
        onTomarFoto = {
            val archivo = ArchivosFotos.crearArchivoNuevo(context)
            rutaPendiente = archivo.absolutePath
            try {
                camara.launch(ArchivosFotos.uriParaCamara(context, archivo))
            } catch (e: ActivityNotFoundException) {
                rutaPendiente = null
                archivo.delete()
                Toast.makeText(context, "No hay una app de cámara disponible", Toast.LENGTH_SHORT).show()
            }
        },
        onElegirDeGaleria = {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    )
}
