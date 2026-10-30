
package picflow.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorPuroTest {

    @Test
    fun recorte_sinCambios_devuelveImagenCompleta() {
        val rectangulo = Recorte().aRectangulo(100, 80)

        assertEquals(0, rectangulo.izquierda)
        assertEquals(0, rectangulo.arriba)
        assertEquals(100, rectangulo.ancho)
        assertEquals(80, rectangulo.alto)
    }

    @Test
    fun recorte_conPorcentajes_calculaDimensionesCorrectas() {
        val recorte = Recorte(
            izquierda = 0.1f,
            derecha = 0.2f,
            arriba = 0.1f,
            abajo = 0.1f
        )

        val rectangulo = recorte.aRectangulo(100, 100)

        assertEquals(10, rectangulo.izquierda)
        assertEquals(10, rectangulo.arriba)
        assertEquals(70, rectangulo.ancho)
        assertEquals(80, rectangulo.alto)
    }

    @Test
    fun recorte_extremo_nuncaGeneraDimensionesCero() {
        val recorte = Recorte(
            izquierda = 0.45f,
            derecha = 0.45f,
            arriba = 0.45f,
            abajo = 0.45f
        )

        val rectangulo = recorte.aRectangulo(100, 100)

        assertTrue(rectangulo.ancho > 0)
        assertTrue(rectangulo.alto > 0)
    }

    @Test
    fun recorte_sinValores_activoEsFalso() {
        assertFalse(Recorte().activo)
    }

    @Test
    fun recorte_conValores_activoEsVerdadero() {
        assertTrue(Recorte(izquierda = 0.1f).activo)
    }

    @Test
    fun matrizIdentidad_noModificaElColor() {
        val resultado = MatrizColor.aplicar(MatrizColor.IDENTIDAD, 100f, 150f, 200f)

        assertEquals(100f, resultado.first, 0.01f)
        assertEquals(150f, resultado.second, 0.01f)
        assertEquals(200f, resultado.third, 0.01f)
    }

    @Test
    fun saturacionCero_convierteColorAGris() {
        val matriz = MatrizColor.saturacion(0f)
        val resultado = MatrizColor.aplicar(matriz, 255f, 0f, 0f)

        assertEquals(resultado.first, resultado.second, 0.01f)
        assertEquals(resultado.second, resultado.third, 0.01f)
    }

    @Test
    fun filtroOriginal_esMatrizIdentidad() {
        val resultado = FiltroColor.ORIGINAL.matriz

        assertEquals(20, resultado.size)
        assertEquals(1f, resultado[0], 0.01f)
        assertEquals(1f, resultado[6], 0.01f)
        assertEquals(1f, resultado[12], 0.01f)
        assertEquals(1f, resultado[18], 0.01f)
    }

    @Test
    fun brilloContraste_sinCambios_esIdentidad() {
        val matriz = MatrizColor.brilloContraste(0f, 1f)

        assertEquals(100f, MatrizColor.aplicar(matriz, 100f, 150f, 200f).first, 0.01f)
    }
}
