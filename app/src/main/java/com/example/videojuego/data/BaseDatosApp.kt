package com.example.videojuego.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Jugador::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class BaseDatosApp : RoomDatabase() {
    abstract fun daoJugador(): DaoJugador
    companion object {

        //Singleton para evitar varias instancias de la base de datos
        @Volatile
        private var INSTANCIA: BaseDatosApp? = null


        fun getBaseDatos(context: Context): BaseDatosApp {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosApp::class.java,
                    "todo_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}