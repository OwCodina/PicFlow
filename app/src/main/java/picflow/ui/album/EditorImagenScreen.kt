
package picflow.ui.album

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import picflow.editor.FiltroColor
import picflow.editor.MatrizColor
import picflow.editor.Recorte
import picflow.viewmodel.EditorUiState

@Composable
fun EditorImagenScreen(
    state: EditorUiState,
    onIzquierda: (Float) -> Unit,
    onDerecha: (Float) -> Unit,
    onArriba: (Float) -> Unit,
    onAbajo: (Float) -> Unit,
    onRotar: () -> Unit,
    onBrillo: (Float) -> Unit,
    onContraste: (Float) -> Unit,
    onSaturacion: (Float) -> Unit,
    onFiltro: (FiltroColor) -> Unit,
    onRestablecer: () -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    var pestana by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Editar foto", style = MaterialTheme.typography.headlineSmall)

        val bitmap = state.bitmap
        when {
            state.cargando -> {
                Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            bitmap == null -> {
                Text(state.error ?: "No se pudo abrir la imagen", color = MaterialTheme.colorScheme.error)
                OutlinedButton(onClick = onCancelar) { Text("Volver") }
            }
            else -> {
                val matriz = MatrizColor.componer(
                    state.filtro, state.brillo, state.contraste, state.saturacion
                )
                val imagenCompose = remember(bitmap) { bitmap.asImageBitmap() }
                val proporcion = bitmap.width.toFloat() / bitmap.height.toFloat()

                Box(
                    modifier = Modifier.fillMaxWidth().height(340.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.aspectRatio(proporcion)) {
                        Image(
                            bitmap = imagenCompose,
                            contentDescription = "Vista previa de la foto",
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.colorMatrix(ColorMatrix(matriz)),
                            modifier = Modifier.fillMaxSize()
                        )
                        if (state.recorte.activo) {
                            CapaRecorte(state.recorte, Modifier.fillMaxSize())
                        }
                    }
                }

                TabRow(selectedTabIndex = pestana) {
                    Tab(selected = pestana == 0, onClick = { pestana = 0 }, text = { Text("Recortar") })
                    Tab(selected = pestana == 1, onClick = { pestana = 1 }, text = { Text("Color") })
                }

                if (pestana == 0) {
                    ControlesRecorte(state, onIzquierda, onDerecha, onArriba, onAbajo, onRotar)
                } else {
                    ControlesColor(state, onBrillo, onContraste, onSaturacion, onFiltro)
                }

                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                OutlinedButton(onClick = onRestablecer, modifier = Modifier.fillMaxWidth()) {
                    Text("Restablecer todo")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onGuardar,
                        enabled = !state.guardando,
                        modifier = Modifier.weight(1f)
                    ) { Text(if (state.guardando) "Guardando..." else "Guardar") }
                    OutlinedButton(
                        onClick = onCancelar,
                        enabled = !state.guardando,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }
                }
            }
        }
    }
}

@Composable
private fun CapaRecorte(recorte: Recorte, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val izq = size.width * recorte.izquierda
        val arr = size.height * recorte.arriba
        val der = size.width * (1f - recorte.derecha)
        val aba = size.height * (1f - recorte.abajo)
        val sombra = Color.Black.copy(alpha = 0.55f)

        drawRect(sombra, Offset(0f, 0f), Size(size.width, arr))
        drawRect(sombra, Offset(0f, aba), Size(size.width, size.height - aba))
        drawRect(sombra, Offset(0f, arr), Size(izq, aba - arr))
        drawRect(sombra, Offset(der, arr), Size(size.width - der, aba - arr))
        drawRect(
            color = Color.White,
            topLeft = Offset(izq, arr),
            size = Size(der - izq, aba - arr),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun ControlesRecorte(
    state: EditorUiState,
    onIzquierda: (Float) -> Unit,
    onDerecha: (Float) -> Unit,
    onArriba: (Float) -> Unit,
    onAbajo: (Float) -> Unit,
    onRotar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ControlDeslizante("Izquierda", state.recorte.izquierda, 0f..Recorte.MAXIMO, onIzquierda) {
            "${(it * 100).toInt()}%"
        }
        ControlDeslizante("Derecha", state.recorte.derecha, 0f..Recorte.MAXIMO, onDerecha) {
            "${(it * 100).toInt()}%"
        }
        ControlDeslizante("Arriba", state.recorte.arriba, 0f..Recorte.MAXIMO, onArriba) {
            "${(it * 100).toInt()}%"
        }
        ControlDeslizante("Abajo", state.recorte.abajo, 0f..Recorte.MAXIMO, onAbajo) {
            "${(it * 100).toInt()}%"
        }
        OutlinedButton(onClick = onRotar) { Text("Rotar 90°") }
        Text("Al rotar, el recorte se reinicia.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ControlesColor(
    state: EditorUiState,
    onBrillo: (Float) -> Unit,
    onContraste: (Float) -> Unit,
    onSaturacion: (Float) -> Unit,
    onFiltro: (FiltroColor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Filtro", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroColor.values().forEach { filtro ->
                FilterChip(
                    selected = state.filtro == filtro,
                    onClick = { onFiltro(filtro) },
                    label = { Text(filtro.etiqueta) }
                )
            }
        }
        ControlDeslizante("Brillo", state.brillo, -1f..1f, onBrillo) { "${(it * 100).toInt()}" }
        ControlDeslizante("Contraste", state.contraste, 0.5f..1.5f, onContraste) {
            "${(it * 100).toInt()}%"
        }
        ControlDeslizante("Saturación", state.saturacion, 0f..2f, onSaturacion) {
            "${(it * 100).toInt()}%"
        }
    }
}

@Composable
private fun ControlDeslizante(
    titulo: String,
    valor: Float,
    rango: ClosedFloatingPointRange<Float>,
    onCambio: (Float) -> Unit,
    formato: (Float) -> String
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
            Text(formato(valor), style = MaterialTheme.typography.bodyMedium)
        }
        Slider(value = valor, onValueChange = onCambio, valueRange = rango)
    }
}
