package picflow.ui.album

import androidx.compose.runtime.Composable
import picflow.viewmodel.EditorViewModel

/** Conecta el editor con su ViewModel. */
@Composable
fun EditorImagenRoute(
    viewModel: EditorViewModel,
    onTerminar: () -> Unit
) {
    EditorImagenScreen(
        state = viewModel.uiState,
        onIzquierda = viewModel::actualizarIzquierda,
        onDerecha = viewModel::actualizarDerecha,
        onArriba = viewModel::actualizarArriba,
        onAbajo = viewModel::actualizarAbajo,
        onRotar = viewModel::rotar,
        onBrillo = viewModel::actualizarBrillo,
        onContraste = viewModel::actualizarContraste,
        onSaturacion = viewModel::actualizarSaturacion,
        onFiltro = viewModel::elegirFiltro,
        onRestablecer = viewModel::restablecer,
        onGuardar = { viewModel.guardar(onTerminar) },
        onCancelar = onTerminar
    )
}
