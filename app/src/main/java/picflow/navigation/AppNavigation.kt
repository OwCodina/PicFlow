package picflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import picflow.data.AlbumRepository
import picflow.data.AppDatabase
import picflow.ui.album.AlbumesScreen
import picflow.ui.album.DetalleAlbumScreen
import picflow.ui.album.FormularioAlbumRoute
import picflow.viewmodel.AlbumViewModel
import picflow.viewmodel.AlbumViewModelFactory

@Serializable
object Albumes

@Serializable
object FormularioAlbum

@Serializable
data class DetalleAlbum(val albumId: Int)

@Composable
fun AppNavigation() {
    val context = LocalContext.current.applicationContext
    val navController = rememberNavController()

    // Una sola instancia para todas las pantallas|| el repositorio usa la base de datos Room
    val factory = remember(context) {
        AlbumViewModelFactory(AlbumRepository(AppDatabase.getInstance(context).albumDao()))
    }
    val viewModel: AlbumViewModel = viewModel(factory = factory)
    val albumes by viewModel.albumes.collectAsState()

    NavHost(navController = navController, startDestination = Albumes) {

        composable<Albumes> {
            AlbumesScreen(
                albumes = albumes,
                onNuevoAlbum = {
                    viewModel.prepararNuevo()
                    navController.navigate(FormularioAlbum)
                },
                onVerDetalle = { id -> navController.navigate(DetalleAlbum(id)) }
            )
        }

        composable<FormularioAlbum> {
            FormularioAlbumRoute(
                viewModel = viewModel,
                onGuardado = { id ->
                    navController.navigate(DetalleAlbum(id)) {
                        popUpTo<Albumes>() // vuelve desde el detalle, lleva al listado
                    }
                }
            )
        }

        composable<DetalleAlbum> { entry ->
            val args: DetalleAlbum = entry.toRoute()
            DetalleAlbumScreen(
                album = albumes.firstOrNull { it.id == args.albumId },
                mensaje = viewModel.uiState.mensaje,
                onEditar = {
                    viewModel.cargarParaEditar(args.albumId)
                    navController.navigate(FormularioAlbum)
                },
                onEliminar = { album ->
                    viewModel.eliminar(album) { navController.popBackStack() }
                },
                onVolver = {
                    viewModel.consumirMensaje()
                    navController.popBackStack()
                }
            )
        }
    }
}
