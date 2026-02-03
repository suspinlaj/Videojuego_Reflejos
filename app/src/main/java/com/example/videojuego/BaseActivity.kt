package com.example.videojuego

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

// Clase de la que heredan las demás para poner transparente la barra de tareas y botones movil
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.TRANSPARENT  // barra de tareas transparente
        window.navigationBarColor = Color.TRANSPARENT  // botones inferiores transparentes
    }
}