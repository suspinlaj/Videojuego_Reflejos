package com.example.videojuego.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.videojuego.data.Jugador
import java.time.LocalDate

class GameActivity : BaseActivity() {
    private lateinit var gameView: GameView
    private lateinit var viewModel: VistaModeloJugador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[VistaModeloJugador::class.java]

        // Inicializar el juego
        gameView = GameView(this)
        setContentView(gameView)

        guardarNombre()

        mostrarDialogoNombre()
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }

    // Detiene el juego
    override fun onPause() {
        super.onPause()
        gameView.pause()
    }

    fun guardarNombre() {

        supportFragmentManager.setFragmentResultListener(
            DialogoDatos.CLAVE_PETICION,
            this
        ) { _, bundle ->

            val nombre = bundle.getString(DialogoDatos.CLAVE_RESULTADO)

            if (nombre == null) {
                finish()
            }
            else if (nombre.isEmpty()) {
                val nuevoJugador = Jugador(
                    nombre = "Sin Nombre",
                    puntuacion = 0,
                    fecha = LocalDate.now()
                )
                viewModel.guardar(nuevoJugador)
                gameView.iniciarPartida()
            }
            else {
                val nuevoJugador = Jugador(
                    nombre = nombre,
                    puntuacion = 0,
                    fecha = LocalDate.now()
                )
                viewModel.guardar(nuevoJugador)
                gameView.iniciarPartida()
            }
        }
    }

    fun mostrarDialogoNombre() {

        // Crear diálogo
        val dialogo = DialogoDatos()

        // Mensaje que sale
        val args = Bundle()
        dialogo.arguments = args

        // Obligar a usar los botones Guardar o Cancelar
        dialogo.isCancelable = false

        // Mostrar diálogo
        dialogo.show(supportFragmentManager, null)

    }
}