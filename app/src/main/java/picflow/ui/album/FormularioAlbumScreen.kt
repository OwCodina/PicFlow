package picflow.ui.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import picflow.viewmodel.AlbumUiState

@Composable
fun FormularioAlbumScreen(
    state: AlbumUiState,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onTipoChange: (String) -> Unit,
    onPrivadoChange: (Boolean) -> Unit,
    onTomarFoto: () -> Unit,
    onQuitarFoto: () -> Unit,
    onGuardar: () -> Unit
) {
    var expandido by rememberSaveable { mutableStateOf(false) }
    val tipos = listOf("Personal", "Familia", "Viajes", "Trabajo")
    val esEdicion = state.editandoId != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (esEdicion) "Editar álbum" else "Crear álbum",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = state.nombre,
            onValueChange = onNombreChange,
            label = { Text("Nombre") },
            isError = state.errorNombre != null,
            supportingText = { state.errorNombre?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción (opcional)") },
            isError = state.errorDescripcion != null,
            supportingText = { state.errorDescripcion?.let { Text(it) } },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            OutlinedButton(
                onClick = { expandido = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.tipo.isBlank()) "Tipo de álbum" else "Tipo: ${state.tipo}")
            }
            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                tipos.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo) },
                        onClick = {
                            onTipoChange(tipo)
                            expandido = false
                        }
                    )
                }
            }
        }
        state.errorTipo?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Álbum privado", modifier = Modifier.weight(1f))
            Switch(checked = state.esPrivado, onCheckedChange = onPrivadoChange)
        }

        // ---- Recurso nativo: cámara. La vista previa aparece con animación ----
        AnimatedVisibility(visible = state.portadaPath != null) {
            state.portadaPath?.let { ruta ->
                AsyncImage(
                    model = File(ruta),
                    contentDescription = "Portada del álbum",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onTomarFoto, modifier = Modifier.weight(1f)) {
                Text(if (state.portadaPath == null) "Tomar foto de portada" else "Cambiar foto")
            }
            if (state.portadaPath != null) {
                TextButton(onClick = onQuitarFoto) { Text("Quitar") }
            }
        }

        Button(
            onClick = onGuardar,
            enabled = !state.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (esEdicion) "Guardar cambios" else "Guardar álbum")
        }
    }
}
