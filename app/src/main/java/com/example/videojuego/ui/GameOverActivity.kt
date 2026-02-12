package com.example.videojuego.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.videojuego.R
import com.example.videojuego.databinding.ActivityGamerOverBinding

class GameOverActivity : BaseActivity() {
    private lateinit var binding: ActivityGamerOverBinding
    private var puntuacion: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamerOverBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        puntuacion = intent.getIntExtra("puntuacion", 0)

        binding.descripcion.text = "Puntuación: $puntuacion"

        gifDerrota()
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