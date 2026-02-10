package com.example.videojuego

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.videojuego.databinding.ActivityGamerOverBinding

class GameOverActivity : BaseActivity() {
    private lateinit var binding: ActivityGamerOverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamerOverBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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