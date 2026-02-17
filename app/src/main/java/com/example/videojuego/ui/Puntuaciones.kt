package com.example.videojuego.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.videojuego.R
import com.example.videojuego.data.Jugador
import com.example.videojuego.databinding.ActivityPuntuacionesBinding
import java.time.format.DateTimeFormatter

class Puntuaciones : BaseActivity() {
    private lateinit var binding: ActivityPuntuacionesBinding
    private val vistaModelo: VistaModeloJugador by viewModels()
    private val listaJugadores = mutableListOf<Jugador>()
    private lateinit var adaptador: AdaptadorPuntuaciones //adaptador personalizado

    private var animacionNubes: AnimacionNubes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntuacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adaptador = AdaptadorPuntuaciones(this, listaJugadores)
        binding.listViewPuntuaciones.adapter = adaptador

        cargarDatos()
        animacionPortada()
    }

    private fun cargarDatos() {
        vistaModelo.getTodosJugadoresLiveData().observe(this, Observer { jugadoresBD ->
            listaJugadores.clear()

            // obtener top 5
            val top5Jugadores = jugadoresBD.sortedByDescending { it.puntuacion }.take(10)

            // añadir solo los 5 jugadores
            listaJugadores.addAll(top5Jugadores)

            adaptador.notifyDataSetChanged()
        })
    }

    fun onClickAtras(view : View) {
        finish()
    }

    private fun animacionPortada() {

        val listaImagenes = listOf(
            R.drawable.nube1,
            R.drawable.nube2,
            R.drawable.nube3,
            R.drawable.nube4,
            R.drawable.nube5,
            R.drawable.nube6,
            R.drawable.nube7,
            R.drawable.nube8,
            R.drawable.nube9
        )

        animacionNubes = AnimacionNubes(
            container = binding.animacion,
            scope = lifecycleScope,
            imagenes = listaImagenes
        )

        // Conectar la animación al ciclo de vida
        lifecycle.addObserver(animacionNubes!!)
    }

}