package com.example.videojuego.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.example.videojuego.R
import com.example.videojuego.data.Jugador
import java.time.LocalDate

class GameActivity : BaseActivity() {
    private lateinit var gameView: GameView
    private lateinit var viewModel: VistaModeloJugador
    private var mediaPlayer: MediaPlayer? = null

    private val handlerMusica = Handler(Looper.getMainLooper())
    private val runnableMusica = Runnable { mediaPlayer?.start() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[VistaModeloJugador::class.java]

        // Inicializar el juego
        gameView = GameView(this)
        setContentView(gameView)

        guardarNombre()
        reproducirAudioInicio()
        mostrarDialogoNombre()
    }

    override fun onStart() {
        super.onStart()
        //música al entrar o volver a la pantallan un seg despues
        handlerMusica.postDelayed(runnableMusica, 1000)

    }

    override fun onStop() {
        super.onStop()

        //cancelar temp musica
        handlerMusica.removeCallbacks(runnableMusica)

        // parar musica
        mediaPlayer?.pause()
    }

    private fun reproducirAudioInicio() {
        mediaPlayer = MediaPlayer.create(this, R.raw.juego)

        // repetir en bucle infinito
        mediaPlayer?.isLooping = true

        // ajutar el volumen a lo que quiero
        val volumen = 0.1f
        mediaPlayer?.setVolume(volumen, volumen)

    }

    //  parar música al salir
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        gameView.destruirSonidos()
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
        supportFragmentManager.setFragmentResultListener(DialogoDatos.CLAVE_PETICION, this) { _, bundle ->

            val nombreRecibido = bundle.getString(DialogoDatos.CLAVE_RESULTADO)

            if (nombreRecibido == null) {
                finish()
            } else {
                val nombreJugador = if (nombreRecibido.isEmpty()) "Sin Nombre" else nombreRecibido

                // Pasamos el nombre al juego
                gameView.iniciarPartida(nombreJugador)
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