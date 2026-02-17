package com.example.videojuego.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.videojuego.R
import com.example.videojuego.data.Jugador
import com.example.videojuego.databinding.ActivityGamerOverBinding
import java.time.LocalDate

class GameOverActivity : BaseActivity() {
    private lateinit var binding: ActivityGamerOverBinding
    private lateinit var viewModel: VistaModeloJugador

    private var mediaPlayer: MediaPlayer? = null

    private val handlerMusica = Handler(Looper.getMainLooper())
    private val runnableMusica = Runnable { mediaPlayer?.start() }
    private var puntuacion: Int = 0
    private var nombreJugador: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamerOverBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[VistaModeloJugador::class.java]

        puntuacion = intent.getIntExtra("puntuacion", 0)
        nombreJugador = intent.getStringExtra("nombreJugador") ?: "Sin Nombre"

        binding.descripcion.text = "Puntuación: $puntuacion"

        crearJugador()
        gifDerrota()
        reproducirAudioInicio()
    }

    fun crearJugador() {

        val jugadorFinal = Jugador(
            nombre = nombreJugador,
            puntuacion = puntuacion,
            fecha = LocalDate.now()
        )
        viewModel.guardar(jugadorFinal)
    }

    fun gifDerrota() {
        val gifImagenView = findViewById<ImageView>(R.id.gifDerrota)

        Glide.with(this)
            .asGif()
            .load(R.raw.gifderrota)
            .into(gifImagenView)
    }

    override fun onStart() {
        super.onStart()
        //música al entrar o volver a la pantallan un seg despues
        handlerMusica.postDelayed(runnableMusica, 2000)

    }

    override fun onStop() {
        super.onStop()

        //cancelar temp musica
        handlerMusica.removeCallbacks(runnableMusica)

        // parar musica
        mediaPlayer?.pause()
    }

    private fun reproducirAudioInicio() {
        mediaPlayer = MediaPlayer.create(this, R.raw.gameover)

        // repetir en bucle infinito
        mediaPlayer?.isLooping = true

        // ajutar el volumen a lo que quiero
        val volumen = 0.1f
        mediaPlayer?.setVolume(volumen, volumen)

        mediaPlayer?.start()
    }

    //  parar música al salir
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun onClickVolver(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}