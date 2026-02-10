package com.example.videojuego

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale

class GameView(context: Context) : SurfaceView(context), Runnable{

    // IMAGEN FONDO
    private var imagenFondo: Bitmap? = null
    private var fondo4Vidas: Bitmap? = null
    private var fondo3Vidas: Bitmap? = null
    private var fondo2Vidas: Bitmap? = null
    private var fondo1Vida: Bitmap? = null

    // IMAGENES SLIMES
    private val listaSlimes = ArrayList<Bitmap>()
    private var slimeActual: Bitmap? = null
    private val margenSlime = 100
    val margenSuperior = 200   //respetar barra de corazones
    val margenLateral = 50

    private var gameOver = false

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
    private val reduccionTiempo: Long = 70 // quitar tiempo cada vez que se acierta

    // ACIERTOS
    private var contadorAciertos = 0
    private val aciertosSubirNivel = 5

    // VIDAS
    private var maxVidas = 5
    private var vidasActuales = 5
    private var corazonLleno: Bitmap? = null
    private var corazonVacio: Bitmap? = null

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

        // cargar imagenes fondo
        imagenFondo  = cargarFondo(R.drawable.fondo2)
        fondo4Vidas  = cargarFondo(R.drawable.fondo4vidas)
        fondo3Vidas  = cargarFondo(R.drawable.fondo3vidas)
        fondo2Vidas  = cargarFondo(R.drawable.fondo2vidas)
        fondo1Vida   = cargarFondo(R.drawable.fondo1vidas)

        // cargar imagenes vidas
        val vidasLlenas = BitmapFactory.decodeResource(resources, R.drawable.vidabien)
        corazonLleno = vidasLlenas.scale(80, 80, false)

        val vidasVacias = BitmapFactory.decodeResource(resources, R.drawable.vidamal)
        corazonVacio = vidasVacias.scale(80, 80, false)

    }

    // para que los fondos ocupen toda la pantalla
    private fun cargarFondo(resId: Int): Bitmap {
        val bmp = BitmapFactory.decodeResource(resources, resId)
        return bmp.scale(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels,
            false
        )
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

        // generar slimes
        if (slimeActual == null && width > 0 && height > 0) {
            generarNuevoSlime()
        }

        // comprobar tiempo que se tarda en pulsar
        if (slimeActual != null) {
            val tiempoActual = System.currentTimeMillis()

            if (tiempoActual - tiempoAparicion > tiempoLimite) {
                vidasActuales--

                if (vidasActuales <= 0 && !gameOver) {
                    gameOver = true
                    playing = false
                    val intent = Intent(context, GameOverActivity::class.java)
                    context.startActivity(intent)
                }

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
            val maxX = width - bmp.width - margenSlime
            val maxY = height - bmp.height - margenSlime

            figuraX = (margenLateral + Math.random() * (width - bmp.width - margenLateral * 2)).toFloat()
            figuraY = (margenSuperior + Math.random() * (height - bmp.height - margenSuperior - margenLateral)).toFloat()

        }
        tiempoAparicion = System.currentTimeMillis()
    }

    private fun draw() {
        if (gameOver) return   // no dibujar nada si el juego terminó

        if (surfaceHolder.surface.isValid) {
            val canvas: Canvas = surfaceHolder.lockCanvas()

                canvas.drawBitmap(imagenFondo!!, 0f, 0f, null)

                when (vidasActuales) {
                    4 -> canvas.drawBitmap(fondo4Vidas!!, 0f, 0f, null)
                    3 -> canvas.drawBitmap(fondo3Vidas!!, 0f, 0f, null)
                    2 -> canvas.drawBitmap(fondo2Vidas!!, 0f, 0f, null)
                    1 -> canvas.drawBitmap(fondo1Vida!!, 0f, 0f, null)
                }

            // poner slime encima del fondo
            slimeActual?.let { bitmap ->
                canvas.drawBitmap(bitmap, figuraX, figuraY, null)
            }

            //  BARRA DE VIDA
            if (corazonLleno != null && corazonVacio != null) {

                for (i in 0 until maxVidas) {


                    val posX = 50f + (i * 90)
                    val posY = 100f // top

                    // poner vida vacia o no
                    if (i < vidasActuales) {
                        canvas.drawBitmap(corazonLleno!!, posX, posY, null)
                    } else {
                        canvas.drawBitmap(corazonVacio!!, posX, posY, null)
                    }
                }
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
                    generarNuevoSlime()

                    // aumentar dificultad si se acierta
                    if (contadorAciertos >= aciertosSubirNivel) {

                        // Reducir tiempo
                        if (tiempoLimite > tiempoMinimo) {
                            tiempoLimite -= reduccionTiempo
                            contadorAciertos = 0
                        }

                }/*else {
                    // restar vida si se falla
                    vidasActuales--


                    // si se pierde
                    if (vidasActuales <= 0) {
                        // parar el bucve del juego
                        playing = false

                        val intent = Intent(context, GamerOverActivity::class.java)
                        context.startActivity(intent)
                    }
                }*/
                }
            }
        }
        return true
    }
}