package picflow.ui.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import picflow.model.Album

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumesScreen(
    albumes: List<Album>,
    onNuevoAlbum: () -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis álbumes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevoAlbum) { Text("+") }
        }
    ) { padding ->
        if (albumes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Todavía no hay álbumes")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albumes, key = { it.id }) { album ->
                    Card(
                        onClick = { onVerDetalle(album.id) },
                        // Animación: los álbumes entran, salen y se reordenan con movimiento suave
                        modifier = Modifier.fillMaxWidth().animateItem()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            album.portadaPath?.let { ruta ->
                                AsyncImage(
                                    model = File(ruta),
                                    contentDescription = "Portada de ${album.nombre}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                                )
                            }
                            Column {
                                Text(album.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(album.tipo)
                            }
                        }
                    }
                }
            }
        }
    }
}
