// ─────────────────────────────────────────────────────────────
// PACKAGE: la "dirección" de este archivo dentro del proyecto
// ─────────────────────────────────────────────────────────────
package com.sanmateo.actividad2

// ─────────────────────────────────────────────────────────────
// IMPORTS: herramientas que traemos de Android
// ─────────────────────────────────────────────────────────────
import android.os.Bundle                          // datos que Android le pasa a la pantalla
import android.text.InputType                     // tipo de teclado (numérico)
import android.view.ViewGroup                     // medidas (WRAP_CONTENT)
import android.widget.Button                      // botón
import android.widget.EditText                    // campo para escribir
import android.widget.LinearLayout                // contenedor que apila elementos
import android.widget.TextView                    // texto para mostrar
import androidx.appcompat.app.AppCompatActivity   // clase base de una pantalla clásica

// ─────────────────────────────────────────────────────────────
// CLASE ColumnaEstudiante: agrupa todo lo de un estudiante
// (su nombre, sus 3 campos de nota y donde se muestra el resultado).
// Es como una "ficha" con varios datos dentro.
// ─────────────────────────────────────────────────────────────
class ColumnaEstudiante(
    val nombre: String,
    val campoNota1: EditText,
    val campoNota2: EditText,
    val campoNota3: EditText,
    val resultado: TextView
)

// ─────────────────────────────────────────────────────────────
// MainActivity: la pantalla que Android abre al iniciar la app
// ─────────────────────────────────────────────────────────────
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── REQ-01: datos que NO cambian → val (inmutables) ──
        val estudiante1: String = "Carlos Jesús Robles Espinosa"
        val estudiante2: String = "Andrés Giovanny Jaime Laverde"
        val carrera: String = "Ingeniería de Sistemas"
        val asignatura: String = "Programación para Dispositivos Móviles"

        // ── Pantalla principal: todo apilado de arriba a abajo ──
        val pantalla = LinearLayout(this)
        pantalla.orientation = LinearLayout.VERTICAL
        pantalla.setPadding(32, 140, 32, 32)

        // Encabezado con interpolación
        val encabezado = TextView(this)
        encabezado.text = "$asignatura\n$carrera"
        encabezado.textSize = 18f
        pantalla.addView(encabezado)

        // ── Fila horizontal: aquí van las dos columnas lado a lado ──
        val fila = LinearLayout(this)
        fila.orientation = LinearLayout.HORIZONTAL
        pantalla.addView(fila)

        val columna1 = crearColumna(fila, estudiante1)
        val columna2 = crearColumna(fila, estudiante2)

        // ── Botón que calcula a los dos ──
        val boton = Button(this)
        boton.text = "Calcular promedios"
        pantalla.addView(boton)

        setContentView(pantalla)

        boton.setOnClickListener {
            calcularEstudiante(columna1)
            calcularEstudiante(columna2)
        }
    }

    // ─────────────────────────────────────────────────────────
    // Crea la columna de un estudiante y la agrega a la fila.
    // Devuelve una ColumnaEstudiante con todo lo creado.
    // ─────────────────────────────────────────────────────────
    private fun crearColumna(fila: LinearLayout, nombre: String): ColumnaEstudiante {
        val columna = LinearLayout(this)
        columna.orientation = LinearLayout.VERTICAL
        columna.setPadding(8, 24, 8, 24)
        // Ancho 0 + peso 1 = las dos columnas se reparten el ancho por mitades
        columna.layoutParams = LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        )
        fila.addView(columna)

        // Nombre del estudiante arriba de su columna
        val titulo = TextView(this)
        titulo.text = nombre
        titulo.textSize = 16f
        columna.addView(titulo)

        // Función local: crea un campo de nota en ESTA columna
        fun crearCampo(pista: String): EditText {
            val campo = EditText(this)
            campo.hint = pista
            campo.inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL   // teclado numérico
            columna.addView(campo)
            return campo
        }

        val nota1 = crearCampo("Corte 1")
        val nota2 = crearCampo("Corte 2")
        val nota3 = crearCampo("Corte 3 (opcional)")

        // Donde se mostrará el resultado de este estudiante
        val resultado = TextView(this)
        resultado.textSize = 15f
        columna.addView(resultado)

        return ColumnaEstudiante(nombre, nota1, nota2, nota3, resultado)
    }

    // ─────────────────────────────────────────────────────────
    // Lee las notas de UNA columna, valida, calcula y muestra.
    // ─────────────────────────────────────────────────────────
    private fun calcularEstudiante(col: ColumnaEstudiante) {

        // ── REQ-02: notas → var (cambian con lo que escribe el usuario) ──
        var notaCorte1: Double = 0.0
        var notaCorte2: Double = 0.0

        // ── REQ-03: nota que puede estar pendiente → Double? ──
        var notaCorte3: Double? = null

        // toDoubleOrNull(): texto → número; vacío o letras → null
        val n1 = col.campoNota1.text.toString().toDoubleOrNull()
        val n2 = col.campoNota2.text.toString().toDoubleOrNull()
        val n3 = col.campoNota3.text.toString().toDoubleOrNull()

        // Validación: cortes 1 y 2 obligatorios, todo entre 0.0 y 5.0
        if (n1 == null || n2 == null || n1 !in 0.0..5.0 || n2 !in 0.0..5.0) {
            col.resultado.text = "Notas 1 y 2: de 0.0 a 5.0"
            return
        }
        if (n3 != null && n3 !in 0.0..5.0) {
            col.resultado.text = "Nota 3: de 0.0 a 5.0"
            return
        }

        notaCorte1 = n1
        notaCorte2 = n2
        notaCorte3 = n3                       // puede quedar null (pendiente)

        // Operador Elvis (?:): si notaCorte3 es null, usa 0.0
        val notaFinal3: Double = notaCorte3 ?: 0.0

        // ── REQ-04 ──
        val promedio = calcularPromedio(notaCorte1, notaCorte2, notaFinal3)
        col.resultado.text = generarReporte(promedio)
    }
}

// ─────────────────────────────────────────────────────────────
// FUNCIÓN DE EXPRESIÓN ÚNICA: promedio ponderado.
// Pesos: corte 1 = 30 %, corte 2 = 30 %, corte 3 = 40 %.
// ─────────────────────────────────────────────────────────────
fun calcularPromedio(n1: Double, n2: Double, n3: Double): Double =
    n1 * 0.30 + n2 * 0.30 + n3 * 0.40

// ─────────────────────────────────────────────────────────────
// Reporte con INTERPOLACIÓN: promedio y estado del estudiante.
// APROBADO si el promedio es >= 3.0; si no, REPROBADO.
// ─────────────────────────────────────────────────────────────
fun generarReporte(promedio: Double): String {
    val estado = if (promedio >= 3.0) "APROBADO" else "REPROBADO"
    return "Promedio: ${String.format("%.2f", promedio)}\nEstado: $estado"
}