package com.example.videojuego.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "jugadores")
data class Jugador(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre : String,
    val puntuacion : Int,
    val fecha : LocalDate
)
