package picflow.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import picflow.data.AlbumRepository
import picflow.data.AppDatabase
import picflow.data.ImagenRepository
import picflow.ui.album.AlbumesScreen
import picflow.ui.album.DetalleAlbumRoute
import picflow.ui.album.EditorImagenRoute
import picflow.ui.album.FormularioAlbumRoute
import picflow.ui.album.VisorImagenScreen
import picflow.viewmodel.AlbumViewModel
import picflow.viewmodel.AlbumViewModelFactory
import picflow.viewmodel.EditorViewModel
import picflow.viewmodel.EditorViewModelFactory
import picflow.viewmodel.ImagenViewModel
import picflow.viewmodel.ImagenViewModelFactory

@Serializable
object Albumes

@Serializable
object FormularioAlbum

@Serializable
data class DetalleAlbum(val albumId: Int)

@Serializable
data class VisorImagen(val imagenId: Int)

@Serializable
data class EditorImagen(val imagenId: Int)

@Composable
fun AppNavigation() {
    val context = LocalContext.current.applicationContext
    val navController = rememberNavController()

    // Los repositorios usan Room. Los ViewModels de álbumes e imágenes se comparten entre pantallas.
    val imagenRepository = remember(context) {
        ImagenRepository(AppDatabase.getInstance(context).imagenDao())
    }
    val albumFactory = remember(context) {
        val db = AppDatabase.getInstance(context)
        AlbumViewModelFactory(AlbumRepository(db.albumDao(), db.imagenDao()))
    }
    val viewModel: AlbumViewModel = viewModel(factory = albumFactory)
    val imagenViewModel: ImagenViewModel = viewModel(factory = ImagenViewModelFactory(imagenRepository))
    val editorFactory = remember(imagenRepository) { EditorViewModelFactory(imagenRepository) }

    val albumes by viewModel.albumes.collectAsState()
    val imagenes by imagenViewModel.imagenes.collectAsState()

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
            // Al salir sin guardar se borra la foto que se haya tomado
            BackHandler {
                viewModel.descartarFormulario()
                navController.popBackStack()
            }
            FormularioAlbumRoute(
                viewModel = viewModel,
                onGuardado = { id ->
                    navController.navigate(DetalleAlbum(id)) {
                        popUpTo<Albumes>() // desde el detalle, "volver" lleva al listado
                    }
                }
            )
        }

        composable<DetalleAlbum> { entry ->
            val args: DetalleAlbum = entry.toRoute()
            val actual = albumes.firstOrNull { it.id == args.albumId }

            // Guarda el último álbum visto para que al eliminar no aparezca
            // "No se encontró el álbum" durante la animación de salida
            var ultimo by remember { mutableStateOf(actual) }
            LaunchedEffect(actual) { if (actual != null) ultimo = actual }

            // Le indica al ViewModel de imágenes qué álbum se está viendo
            LaunchedEffect(args.albumId) { imagenViewModel.seleccionarAlbum(args.albumId) }

            // Sirve para el botón Volver y para el botón atrás del sistema:
            // en ambos casos se limpia el mensaje de éxito
            val volver: () -> Unit = {
                viewModel.consumirMensaje()
                navController.popBackStack()
            }
            BackHandler(onBack = volver)

            DetalleAlbumRoute(
                albumId = args.albumId,
                album = actual ?: ultimo,
                // Se filtra por álbum para no mostrar un instante las fotos del álbum anterior
                imagenes = imagenes.filter { it.albumId == args.albumId },
                imagenViewModel = imagenViewModel,
                mensaje = viewModel.uiState.mensaje,
                onEditar = {
                    viewModel.cargarParaEditar(args.albumId)
                    navController.navigate(FormularioAlbum)
                },
                onEliminar = { album ->
                    volver()
                    viewModel.eliminar(album) {}
                },
                onVolver = volver,
                onAbrirImagen = { id -> navController.navigate(VisorImagen(id)) }
            )
        }

        composable<VisorImagen> { entry ->
            val args: VisorImagen = entry.toRoute()
            val actual = imagenes.firstOrNull { it.id == args.imagenId }

            // Igual que en el álbum: evita el mensaje de "no encontrada" al eliminar
            var ultima by remember { mutableStateOf(actual) }
            LaunchedEffect(actual) { if (actual != null) ultima = actual }

            VisorImagenScreen(
                imagen = actual ?: ultima,
                onEditar = { navController.navigate(EditorImagen(args.imagenId)) },
                onRestaurar = { imagenViewModel.restaurarOriginal(it) },
                onEliminar = { imagen ->
                    navController.popBackStack()
                    imagenViewModel.eliminarImagen(imagen)
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable<EditorImagen> { entry ->
            val args: EditorImagen = entry.toRoute()
            // Un EditorViewModel nuevo por cada vez que se abre el editor
            val editorViewModel: EditorViewModel = viewModel(factory = editorFactory)
            LaunchedEffect(args.imagenId) { editorViewModel.cargar(args.imagenId) }

            EditorImagenRoute(
                viewModel = editorViewModel,
                onTerminar = { navController.popBackStack() }
            )
        }
    }
}