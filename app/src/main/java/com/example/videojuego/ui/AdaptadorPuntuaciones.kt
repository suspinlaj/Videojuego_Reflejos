package com.example.videojuego.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.videojuego.R
import com.example.videojuego.data.Jugador
import java.time.format.DateTimeFormatter

class AdaptadorPuntuaciones(context: Context, private val datos: List<Jugador>) :
    ArrayAdapter<Jugador>(context, 0, datos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var vistaFila = convertView

        if (vistaFila == null) {
            vistaFila = LayoutInflater.from(context).inflate(R.layout.lista_puntuaciones, parent, false)
        }

        val jugadorActual = datos[position]

        val tvNombre = vistaFila!!.findViewById<TextView>(R.id.tvNombre)
        val tvPuntuacion = vistaFila.findViewById<TextView>(R.id.tvPuntuacion)
        val tvFecha = vistaFila.findViewById<TextView>(R.id.tvFecha)

        tvNombre.text = jugadorActual.nombre
        tvPuntuacion.text = "${jugadorActual.puntuacion} pts"

        // calcular fecha
        val formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaAlReves = jugadorActual.fecha.format(formateador)
        tvFecha.text = "$fechaAlReves"

        return vistaFila
    }
}