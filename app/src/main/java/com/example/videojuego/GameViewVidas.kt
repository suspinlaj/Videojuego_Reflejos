package com.example.videojuego

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class GameViewVidas(context: Context) : SurfaceView(context), Runnable{

    // IMAGEN FONDO
    private var imagenFondo: Bitmap? = null
    private var fondoEscalado = false

    // IMAGENES SLIMES
    private val listaSlimes = ArrayList<Bitmap>()
    private var slimeActual: Bitmap? = null

    // Hilo del juego
    private var gameThread: Thread? = null

    // Variable para controlar si estamos jugando
    @Volatile
    private var playing = false

    // Objetos para dibujar
    private val surfaceHolder: SurfaceHolder = holder
    private val paint = Paint()

    // VARIABLES DEL JUEGO
    private var figuraX = 0f
    private var figuraY = 0f
    private var radio = 100f // Tamaño figura

    // TIEMPO
    private var tiempoAparicion: Long = 0  // tiempo aparición slime actual
    private var tiempoLimite: Long = 2000
    private val tiempoMinimo: Long = 500
    private val reduccionTiempo: Long = 40 // quitar tiempo cada vez que se acierta

    private var contadorAciertos = 0
    private val aciertosSubirNivel = 5

    // Inicializar posiciones
    init {
        // Tamaño dibujos slimes
        val ancho = 180
        val alto = 150

        val slimes = listOf(
            R.drawable.slime1bien,
            R.drawable.slime2bien,
            R.drawable.slime3bien,
            R.drawable.slime4bien,
            R.drawable.slime5bien,
            R.drawable.slime6bien,
            R.drawable.slime7bien,
            R.drawable.slimeespecialbien
        )

        // Bucle para poner el tamaño a todas las imagenes
        for (id in slimes) {
            val bitmapOriginal = BitmapFactory.decodeResource(resources, id)
            val bitmapEscalado = Bitmap.createScaledBitmap(bitmapOriginal, ancho, alto, false)

            listaSlimes.add(bitmapEscalado)
        }

        // cargar imagen fondo
        imagenFondo = BitmapFactory.decodeResource(resources, R.drawable.fondo2)

    }

    // para saber cuando poner en funcionamiento el juego
    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    // Controlar para no gastar bateria si el usuario sale del juego
    fun pause() {
        playing = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        while (playing) {
            update()
            draw()
            control()
        }
    }


    private fun update() {
        // escalar la imagen del fondo
        if (!fondoEscalado && width > 0 && imagenFondo != null) {
            // para que ocupe toda la pantalla
            imagenFondo = Bitmap.createScaledBitmap(imagenFondo!!, width, height, false)
            fondoEscalado = true
        }

        // generar slimes
        if (slimeActual == null) { //  && width > 0
            generarNuevoSlime()
        }

        // comprobar tiempo que se tarda en pulsar
        if (slimeActual != null) {
            val tiempoActual = System.currentTimeMillis()

            if (tiempoActual - tiempoAparicion > tiempoLimite) {
                generarNuevoSlime()

            }
        }
    }

    private fun control() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    // Generar un slime y su posicion aleatoria
    private fun generarNuevoSlime() {
        if (listaSlimes.isEmpty()) return

        // Slime aleatorio
        val indiceAleatorio = (Math.random() * listaSlimes.size).toInt()
        slimeActual = listaSlimes[indiceAleatorio]

        // posición aleatoria
        slimeActual?.let { bmp ->
            // para que no se salga de pantalla
            val maxX = width - bmp.width
            val maxY = height - bmp.height

            figuraX = (Math.random() * (if (maxX > 0) maxX else 1)).toFloat()
            figuraY = (Math.random() * (if (maxY > 0) maxY else 1)).toFloat()
        }
        tiempoAparicion = System.currentTimeMillis()
    }

    private fun draw() {
        if (surfaceHolder.surface.isValid) {
            val canvas: Canvas = surfaceHolder.lockCanvas()

            if (imagenFondo != null && fondoEscalado) {
                canvas.drawBitmap(imagenFondo!!, 0f, 0f, null)
            } else {
                canvas.drawColor(Color.GREEN)
            }

            // poner slime encima del fondo
            slimeActual?.let { bitmap ->
                canvas.drawBitmap(bitmap, figuraX, figuraY, null)
            }

            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    // click en la pantalla
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val dedoX = event.x
            val dedoY = event.y

            slimeActual?.let { bitmap ->
                // Comprobar click dedo
                if (dedoX >= figuraX && dedoX <= (figuraX + bitmap.width) &&
                    dedoY >= figuraY && dedoY <= (figuraY + bitmap.height)) {

                    contadorAciertos++

                    // aumentar dificultad si se acierta
                    if (contadorAciertos >= aciertosSubirNivel) {

                        // Reducir tiempo
                        if (tiempoLimite > tiempoMinimo) {
                            tiempoLimite -= reduccionTiempo
                            println("¡NIVEL SUBIDO! Nuevo tiempo: $tiempoLimite ms")
                        }

                        contadorAciertos = 0
                    }
                    generarNuevoSlime()
                }
            }
        }
        return true
    }
}