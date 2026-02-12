package com.example.videojuego.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

// Clase para que hereden las demás pantallas para hacer transparente los botones y barra de tareas
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.TRANSPARENT  // barra de tareas transparente
        window.navigationBarColor = Color.TRANSPARENT  // botones inferiores transparentes
    }
}