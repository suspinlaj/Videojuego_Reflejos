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
import com.example.videojuego.R
import com.example.videojuego.data.Jugador
import com.example.videojuego.databinding.ActivityPuntuacionesBinding
import java.time.format.DateTimeFormatter

class Puntuaciones : BaseActivity() {
    private lateinit var binding: ActivityPuntuacionesBinding
    private val vistaModelo: VistaModeloJugador by viewModels()
    private val listaJugadores = mutableListOf<Jugador>()
    private lateinit var adaptador: AdaptadorPuntuaciones //adaptador personalizado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntuacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adaptador = AdaptadorPuntuaciones(this, listaJugadores)
        binding.listViewPuntuaciones.adapter = adaptador

        cargarDatos()
    }

    private fun cargarDatos() {
        vistaModelo.getTodosJugadoresLiveData().observe(this, Observer { jugadoresBD ->
            listaJugadores.clear()

            // obtener top 5
            val top5Jugadores = jugadoresBD.sortedByDescending { it.puntuacion }.take(5)

            // añadir solo los 5 jugadores
            listaJugadores.addAll(top5Jugadores)

            adaptador.notifyDataSetChanged()
        })
    }

    inner class AdaptadorPuntuaciones(context: Context, private val datos: List<Jugador>) :
        ArrayAdapter<Jugador>(context, 0, datos) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var vistaFila = convertView

            if (vistaFila == null) {
                vistaFila = LayoutInflater.from(context).inflate(R.layout.lista_puntuaciones, parent, false)
            }

            val jugadorActual = datos[position]

            val tvNombrePuntos = vistaFila!!.findViewById<TextView>(R.id.tvNombrePuntos)
            val tvFecha = vistaFila.findViewById<TextView>(R.id.tvFecha)

            // DATOS
            tvNombrePuntos.text = "${jugadorActual.nombre} - ${jugadorActual.puntuacion} pts"

            // FECHA
            val formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            val fechaAlReves = jugadorActual.fecha.format(formateador)

            tvFecha.text = "$fechaAlReves"

            return vistaFila
        }
    }
}