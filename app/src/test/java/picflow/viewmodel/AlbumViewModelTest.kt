package picflow.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import picflow.data.AlbumRepository
import picflow.data.FakeAlbumDao
import picflow.data.FakeImagenDao

class AlbumViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private fun nuevoVm() =
        AlbumViewModel(AlbumRepository(FakeAlbumDao(), FakeImagenDao()))

    private fun vmConDatos(nombre: String, tipo: String = "Viajes") =
        nuevoVm().apply {
            actualizarNombre(nombre)
            actualizarTipo(tipo)
        }

    /** Ejecuta guardar() y devuelve el id si se guardó, o null si hubo errores. */
    private fun AlbumViewModel.guardarYObtenerId(): Int? {
        var id: Int? = null
        guardar { id = it }
        return id
    }

    @Test
    fun caso_valido_crea_album_y_devuelve_id() {
        val vm = vmConDatos("Verano 2026")
        val id = vm.guardarYObtenerId()
        assertNotNull(id)
        assertEquals(1, vm.albumes.value.size)
        assertEquals("Verano 2026", vm.albumes.value.first().nombre)
        assertEquals("Álbum creado correctamente", vm.uiState.mensaje)
    }

    @Test
    fun caso_invalido_nombre_vacio_no_guarda() {
        val vm = vmConDatos("")
        assertNull(vm.guardarYObtenerId())
        assertEquals(0, vm.albumes.value.size)
        assertEquals("El nombre es obligatorio", vm.uiState.errorNombre)
    }

    @Test
    fun caso_invalido_sin_tipo() {
        val vm = vmConDatos("Fotos", tipo = "")
        assertNull(vm.guardarYObtenerId())
        assertEquals("Selecciona un tipo de álbum", vm.uiState.errorTipo)
    }

    @Test
    fun limite_nombre_de_60_caracteres_es_valido_y_61_no() {
        assertNotNull(vmConDatos("a".repeat(60)).guardarYObtenerId())
        val vm = vmConDatos("a".repeat(61))
        assertNull(vm.guardarYObtenerId())
        assertEquals("Usa máximo 60 caracteres", vm.uiState.errorNombre)
    }

    @Test
    fun regla_propia_no_permite_nombres_duplicados() {
        val vm = vmConDatos("Familia")
        assertNotNull(vm.guardarYObtenerId())
        vm.prepararNuevo()
        vm.actualizarNombre("familia")
        vm.actualizarTipo("Familia")
        assertNull(vm.guardarYObtenerId())
        assertEquals("Ya existe un álbum con ese nombre", vm.uiState.errorNombre)
    }

    @Test
    fun el_error_no_borra_los_datos_escritos() {
        val vm = vmConDatos("ab")
        vm.actualizarDescripcion("mi descripción")
        assertNull(vm.guardarYObtenerId())
        assertEquals("ab", vm.uiState.nombre)
        assertEquals("mi descripción", vm.uiState.descripcion)
    }

    @Test
    fun editar_actualiza_el_album_sin_crear_otro() {
        val vm = vmConDatos("Original")
        val id = vm.guardarYObtenerId()!!

        vm.cargarParaEditar(id)
        assertEquals("Original", vm.uiState.nombre)
        vm.actualizarNombre("Editado")

        assertEquals(id, vm.guardarYObtenerId())
        assertEquals(1, vm.albumes.value.size)
        assertEquals("Editado", vm.albumes.value.first().nombre)
        assertEquals("Álbum actualizado correctamente", vm.uiState.mensaje)
    }

    @Test
    fun editar_con_el_mismo_nombre_no_cuenta_como_duplicado() {
        val vm = vmConDatos("Trabajo 1")
        val id = vm.guardarYObtenerId()!!

        vm.cargarParaEditar(id)
        vm.actualizarDescripcion("nueva descripción")

        assertNotNull(vm.guardarYObtenerId())
        assertNull(vm.uiState.errorNombre)
    }

    @Test
    fun eliminar_quita_el_album_de_la_lista() {
        val vm = vmConDatos("Para borrar")
        vm.guardarYObtenerId()
        val album = vm.albumes.value.first()

        vm.eliminar(album) {}

        assertEquals(0, vm.albumes.value.size)
    }

    @Test
    fun si_falla_la_base_de_datos_el_boton_se_libera_y_se_muestra_error() {
        val dao = FakeAlbumDao().apply { fallarAlInsertar = true }
        val vm = AlbumViewModel(AlbumRepository(dao, FakeImagenDao())).apply {
            actualizarNombre("Fotos")
            actualizarTipo("Viajes")
        }

        assertNull(vm.guardarYObtenerId())

        assertFalse(vm.uiState.guardando)
        assertNotNull(vm.uiState.errorGeneral)
        assertEquals("Fotos", vm.uiState.nombre)
        assertEquals(0, vm.albumes.value.size)
    }

    @Test
    fun descartar_formulario_limpia_el_estado() {
        val vm = vmConDatos("Sin guardar")
        vm.actualizarDescripcion("texto")

        vm.descartarFormulario()

        assertEquals("", vm.uiState.nombre)
        assertEquals("", vm.uiState.descripcion)
        assertNull(vm.uiState.editandoId)
    }

    @Test
    fun consumir_mensaje_limpia_el_mensaje_de_exito() {
        val vm = vmConDatos("Verano")
        vm.guardarYObtenerId()
        assertNotNull(vm.uiState.mensaje)

        vm.consumirMensaje()

        assertNull(vm.uiState.mensaje)
    }
}
