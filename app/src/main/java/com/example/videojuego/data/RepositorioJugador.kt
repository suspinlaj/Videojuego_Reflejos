package com.example.videojuego.data

import androidx.lifecycle.LiveData

class RepositorioJugador(private val daoJugador: DaoJugador) {
    val todosJugadores: LiveData<List<Jugador>> = daoJugador.getAllJugadores()

    suspend fun guardar(jugador: Jugador){
        daoJugador.guardar(jugador)
    }
}