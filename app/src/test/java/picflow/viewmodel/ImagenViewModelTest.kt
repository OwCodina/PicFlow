package picflow.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import picflow.data.FakeImagenDao
import picflow.data.ImagenRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ImagenViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeImagenDao
    private lateinit var repository: ImagenRepository
    private lateinit var viewModel: ImagenViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
        dao = FakeImagenDao()
        repository = ImagenRepository(dao)
        viewModel = ImagenViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun agregarFoto_guardaLaImagenEnElAlbum() = runTest(dispatcher) {
        viewModel.seleccionarAlbum(1)
        viewModel.agregarFoto(1, "/fotos/foto1.jpg")
        advanceUntilIdle()

        val fotos = viewModel.imagenes.value
        assertEquals(1, fotos.size)
        assertEquals("/fotos/foto1.jpg", fotos.first().rutaOriginal)
        assertEquals(1, fotos.first().albumId)
    }

    @Test
    fun eliminarImagen_quitaLaFotoDelAlbum() = runTest(dispatcher) {
        viewModel.agregarFoto(1, "/fotos/foto1.jpg")
        advanceUntilIdle()

        val imagen = repository.observarPorAlbum(1).first().first()
        viewModel.eliminarImagen(imagen)
        advanceUntilIdle()

        assertEquals(0, viewModel.imagenes.value.size)
    }

    @Test
    fun seleccionarAlbum_muestraSoloSusFotografias() = runTest(dispatcher) {
        viewModel.agregarFoto(1, "/fotos/foto1.jpg")
        viewModel.agregarFoto(2, "/fotos/foto2.jpg")
        advanceUntilIdle()

        viewModel.seleccionarAlbum(1)
        advanceUntilIdle()

        assertEquals(1, viewModel.imagenes.value.size)
        assertEquals(1, viewModel.imagenes.value.first().albumId)
    }

    @Test
    fun obtenerImagenPorId_devuelveLaFotoGuardada() = runTest(dispatcher) {
        viewModel.agregarFoto(1, "/fotos/foto1.jpg")
        advanceUntilIdle()

        val imagen = repository.observarPorAlbum(1).first().first()
        val resultado = repository.obtenerPorId(imagen.id)

        assertNotNull(resultado)
        assertEquals(imagen.id, resultado?.id)
    }
}
