package com.example.videojuego.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "jugadores")
data class Jugador(
    val nombre : String,
    val puntuacion : Int,
    @PrimaryKey
    val fecha : LocalDate
)
