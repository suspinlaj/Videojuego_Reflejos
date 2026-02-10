package com.example.videojuego

import android.os.Bundle

class GameActivity : BaseActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar el juego
        gameView = GameView(this)
        setContentView(gameView)
    }

    // Empieza el juego
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