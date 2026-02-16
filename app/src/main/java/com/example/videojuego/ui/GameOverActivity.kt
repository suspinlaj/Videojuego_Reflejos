package com.example.videojuego.ui

import android.content.Intent
import android.os.Bundle
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

    fun onClickVolver(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}