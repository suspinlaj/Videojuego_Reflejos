package com.example.videojuego.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.videojuego.data.Jugador
import com.example.videojuego.databinding.ActivityPuntuacionesBinding

class Puntuaciones : BaseActivity() {
    private lateinit var binding: ActivityPuntuacionesBinding
    private val vistaModelo: VistaModeloJugador by viewModels()
    private lateinit var adaptador: ArrayAdapter<String>
    private val listaVisualizar = mutableListOf<String>() // contenido para el ArrayAdapter
    private val mapeoJugador = mutableListOf<Jugador>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntuacionesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaVisualizar)
        binding.listViewPuntuaciones.adapter = adaptador

        cargarDatos()
    }

    fun cargarDatos() {
        // Observamos LiveData desde la BD
        val jugadorLiveData = vistaModelo.getTodosJugadoresLiveData()
        jugadorLiveData.observe(this, Observer { jugador ->

            // Actualizamos la lista para el ArrayAdapter
            listaVisualizar.clear()
            mapeoJugador.clear()
            jugador.forEach { j ->
                mapeoJugador.add(j)
            }
            adaptador.notifyDataSetChanged()
        })
    }
}