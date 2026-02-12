package com.example.videojuego.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoJugador {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardar(jugador: Jugador) : Long

    @Query("SELECT * FROM jugadores ORDER BY fecha DESC")
    fun getAllJugadores(): LiveData<List<Jugador>>
}