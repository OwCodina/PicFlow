package picflow.ui.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import picflow.model.Album

@Composable
fun DetalleAlbumScreen(
    album: Album?,
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: (Album) -> Unit,
    mensaje: String? = null
) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    // Animación de confirmación: el mensaje de éxito aparece suavemente
    var mensajeVisible by remember { mutableStateOf(false) }
    LaunchedEffect(mensaje) { mensajeVisible = mensaje != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Detalle del álbum", style = MaterialTheme.typography.headlineSmall)

        AnimatedVisibility(
            visible = mensajeVisible,
            enter = fadeIn() + expandVertically()
        ) {
            Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.primary)
        }

        if (album == null) {
            Text("No se encontró el álbum")
        } else {
            album.portadaPath?.let { ruta ->
                AsyncImage(
                    model = File(ruta),
                    contentDescription = "Portada del álbum",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            Text(album.nombre, style = MaterialTheme.typography.titleLarge)
            Text("Tipo: ${album.tipo}")
            Text(if (album.esPrivado) "Álbum privado" else "Álbum público")
            if (album.descripcion.isNotBlank()) Text(album.descripcion)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditar) { Text("Editar") }
                OutlinedButton(onClick = { confirmarEliminar = true }) { Text("Eliminar") }
            }
        }

        OutlinedButton(onClick = onVolver) { Text("Volver") }
    }

    if (confirmarEliminar && album != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar álbum") },
            text = { Text("Se eliminará \"${album.nombre}\". Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar(album)
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}
