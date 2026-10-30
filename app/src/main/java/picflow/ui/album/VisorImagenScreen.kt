
package picflow.ui.album

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
import picflow.model.Imagen

@Composable
fun VisorImagenScreen(
    imagen: Imagen?,
    onEditar: () -> Unit,
    onRestaurar: (Imagen) -> Unit,
    onEliminar: (Imagen) -> Unit,
    onVolver: () -> Unit
) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Foto", style = MaterialTheme.typography.headlineSmall)

        if (imagen == null) {
            Text("No se encontró la foto")
        } else {
            AsyncImage(
                model = File(imagen.rutaVisible),
                contentDescription = "Foto del álbum",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Text(if (imagen.rutaEditada != null) "Versión editada" else "Foto original")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditar) { Text("Editar") }
                OutlinedButton(onClick = { confirmarEliminar = true }) { Text("Eliminar") }
            }
            if (imagen.rutaEditada != null) {
                OutlinedButton(onClick = { onRestaurar(imagen) }) { Text("Restaurar original") }
            }
        }

        OutlinedButton(onClick = onVolver) { Text("Volver") }
    }

    if (confirmarEliminar && imagen != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar foto") },
            text = { Text("Se eliminará la foto y su versión editada. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar(imagen)
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}
