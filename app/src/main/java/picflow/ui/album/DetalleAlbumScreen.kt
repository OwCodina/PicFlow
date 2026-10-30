
package picflow.ui.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import picflow.model.Album
import picflow.model.Imagen

// Hace que un elemento ocupe todo el ancho de la grilla.
private val anchoCompleto: LazyGridItemSpanScope.() -> GridItemSpan = {
    GridItemSpan(maxLineSpan)
}

@Composable
fun DetalleAlbumScreen(
    album: Album?,
    imagenes: List<Imagen>,
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: (Album) -> Unit,
    onTomarFoto: () -> Unit,
    onElegirDeGaleria: () -> Unit,
    onAbrirImagen: (Int) -> Unit,
    mensaje: String? = null
) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    var mensajeVisible by remember { mutableStateOf(false) }
    LaunchedEffect(mensaje) { mensajeVisible = mensaje != null }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = anchoCompleto) {
            Text("Detalle del álbum", style = MaterialTheme.typography.headlineSmall)
        }

        item(span = anchoCompleto) {
            AnimatedVisibility(
                visible = mensajeVisible,
                enter = fadeIn() + expandVertically()
            ) {
                Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.primary)
            }
        }

        if (album == null) {
            item(span = anchoCompleto) { Text("No se encontró el álbum") }
        } else {
            album.portadaPath?.let { ruta ->
                item(span = anchoCompleto) {
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
            }

            item(span = anchoCompleto) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(album.nombre, style = MaterialTheme.typography.titleLarge)
                    Text("Tipo: ${album.tipo}")
                    Text(if (album.esPrivado) "Álbum privado" else "Álbum público")
                    if (album.descripcion.isNotBlank()) Text(album.descripcion)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onEditar) { Text("Editar") }
                        OutlinedButton(onClick = { confirmarEliminar = true }) {
                            Text("Eliminar")
                        }
                    }
                }
            }

            item(span = anchoCompleto) {
                Text(
                    "Fotos (${imagenes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item(span = anchoCompleto) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onTomarFoto) { Text("Tomar foto") }
                    OutlinedButton(onClick = onElegirDeGaleria) {
                        Text("Desde galería")
                    }
                }
            }

            if (imagenes.isEmpty()) {
                item(span = anchoCompleto) {
                    Text("Este álbum todavía no tiene fotos")
                }
            } else {
                items(imagenes, key = { it.id }) { imagen ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAbrirImagen(imagen.id) }
                    ) {
                        AsyncImage(
                            model = File(imagen.rutaVisible),
                            contentDescription = "Foto del álbum",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (imagen.rutaEditada != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topEnd = 8.dp),
                                modifier = Modifier.align(Alignment.BottomStart)
                            ) {
                                Text(
                                    "Editada",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(
                                        horizontal = 6.dp,
                                        vertical = 2.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item(span = anchoCompleto) {
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Volver")
            }
        }
    }

    if (confirmarEliminar && album != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar álbum") },
            text = {
                Text("Se eliminará \"${album.nombre}\" junto con sus fotos. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar(album)
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
