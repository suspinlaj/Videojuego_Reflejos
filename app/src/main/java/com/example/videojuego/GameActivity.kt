package com.example.videojuego

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videojuego.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameViewVidas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar el juego
        gameView = GameViewVidas(this)
        setContentView(gameView)
    }

    // Arranca el juego
    override fun onResume() {
        super.onResume()
        gameView.resume()
    }

    // Detiene el juego
    override fun onPause() {
        super.onPause()
        gameView.pause()
    }
}