package com.example.videojuego.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.videojuego.data.BaseDatosApp
import com.example.videojuego.data.Jugador
import com.example.videojuego.data.RepositorioJugador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VistaModeloJugador(application: Application) : AndroidViewModel(application) {
    private val repositorio : RepositorioJugador
    val todosJugadores = mutableListOf<Jugador>()

    init {
        val dao = BaseDatosApp.getBaseDatos(application).daoJugador()
        repositorio = RepositorioJugador(dao)
    }

    fun guardar(jugador: Jugador) = viewModelScope.launch(Dispatchers.IO) {
        repositorio.guardar(jugador)
    }

    fun getTodosJugadoresLiveData() =
        BaseDatosApp.getBaseDatos(getApplication()).daoJugador().getAllJugadores()
}