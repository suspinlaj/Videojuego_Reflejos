package com.example.videojuego.ui

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
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.core.graphics.scale
import com.example.videojuego.R

class GameView(context: Context) : SurfaceView(context), Runnable{

    // IMAGEN FONDO
    private var imagenFondo: Bitmap? = null
    private var fondo4Vidas: Bitmap? = null
    private var fondo3Vidas: Bitmap? = null
    private var fondo2Vidas: Bitmap? = null
    private var fondo1Vida: Bitmap? = null

    // IMAGENES SLIMES
    private val listaSlimesBien = ArrayList<Bitmap>()
    private val listaSlimesMal = ArrayList<Bitmap>()
    private var slimeActual: Bitmap? = null
    private val margenSlime = 100

    val margenSuperior = 200   //respetar barra de corazones
    val margenLateral = 50
    private var slimeActualEsEspecial = false

    data class ManchaSlime(
        val bitmap: Bitmap,
        val x: Float,
        val y: Float,
        val tiempoGolpe: Long
    )
    
    private var gameOver = false

    // Hilo del juego
    private var gameThread: Thread? = null

    // para para controlar el bucle infinito del juego.
    @Volatile
    private var jugando = false

    // para controlar si estamos jugando
    @Volatile
    private var juegoIniciado = false

    // Objetos para dibujar
    private val surfaceHolder: SurfaceHolder = holder
    private val paint = Paint()

    // VARIABLES DEL JUEGO
    private var figuraX = 0f
    private var figuraY = 0f
    private var radio = 100f // Tamaño figura

    private var indiceSlimeActual: Int = -1 //  número de slime de la lista
    private val listaManchas = ArrayList<ManchaSlime>()

    // TIEMPO
    private var tiempoAparicion: Long = 0  // tiempo aparición slime actual
    private var tiempoLimite: Long = 2000
    private val tiempoMinimo: Long = 500
    private val reduccionTiempo: Long = 60 // quitar tiempo cada vez que se acierta

    // ACIERTOS
    private var contadorAciertos = 0
    private var aciertosSubirNivel = 5
    private var fuentePuntos: Typeface? = null


    // VIDAS
    private var maxVidas = 5
    private var vidasActuales = 5
    private var corazonLleno: Bitmap? = null
    private var corazonVacio: Bitmap? = null

    // PUNTUACIÓN
    private var puntuacion = 0
    private var puntosPorAcierto = 10

    private var nombreJugador: String = ""

    // Imagenes CUENTA ATRAS
    private var img1: Bitmap? = null
    private var img2: Bitmap? = null
    private var img3: Bitmap? = null

    // CUENTA ATRÁS
    private var enCuentaAtras = false
    private var numeroCuentaAtras = 3
    private var tiempoCambioNumero: Long = 0

    // SONIDOS
    private var soundPool: SoundPool? = null
    private var sonidoEspecialId: Int = 0
    private var sonidoPerderId: Int = 0
    private var sonidoCuentaAtras: Int = 0

    // controlar la carga
    @Volatile
    private var cargandoRecursos = true

    // iniciar recursos
    init {
        try {
            fuentePuntos = resources.getFont(R.font.pixeltitulo)
        } catch (e: Exception) {
            paint.typeface = Typeface.DEFAULT_BOLD
        }

        // cargar y escalar fondos
        imagenFondo = cargarFondo(R.drawable.fondo2)

        // SONIDOS
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // soinar hasta 5 efectos a la vez sin cortarse
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Cargar audios
        sonidoEspecialId = soundPool?.load(context, R.raw.victoria, 1) ?: 0
        sonidoPerderId = soundPool?.load(context, R.raw.derrota, 1) ?: 0
        sonidoCuentaAtras = soundPool?.load(context, R.raw.cuentaatras, 1) ?: 0

    }

    // cargar imagenes no urgentes para que no me pete la app
    private fun cargarRecursos() {
        // Tamaño dibujos slimes
        val ancho = 180
        val alto = 150

        // SLIMES BIEN
        val slimesBien = listOf(
            R.drawable.slime1bien,
            R.drawable.slime2bien,
            R.drawable.slime3bien,
            R.drawable.slime4bien,
            R.drawable.slime5bien,
            R.drawable.slime6bien,
            R.drawable.slime7bien
        )

        val slimeEspecialBien = R.drawable.slimeespecialbien

        // Bucle para poner el tamaño a todas las imagenes
        for (id in slimesBien) {
            val bitmapOriginal = BitmapFactory.decodeResource(resources, id)
            val bitmapEscalado = bitmapOriginal.scale(ancho, alto, false)

            listaSlimesBien.add(bitmapEscalado)
        }

        // SLIMES MAL
        val slimesMal = listOf(
            R.drawable.slime1mal,
            R.drawable.slime2mal,
            R.drawable.slime3mal,
            R.drawable.slime4mal,
            R.drawable.slime5mal,
            R.drawable.slime6mal,
            R.drawable.slime7mal
        )

        val slimeEspecialMal = R.drawable.slimeespecialmal

        // Bucle para poner el tamaño a todas las imagenes
        for (id in slimesMal) {
            val bitmapOriginal = BitmapFactory.decodeResource(resources, id)
            val bitmapEscalado = bitmapOriginal.scale(ancho, alto, false)

            listaSlimesMal.add(bitmapEscalado)
        }


        // fondos dependiendo vida
        fondo4Vidas = cargarFondo(R.drawable.fondo4vidas)
        fondo3Vidas = cargarFondo(R.drawable.fondo3vidas)
        fondo2Vidas = cargarFondo(R.drawable.fondo2vidas)
        fondo1Vida  = cargarFondo(R.drawable.fondo1vidas)


        // cargar imagenes vidas
        val vidasLlenas = BitmapFactory.decodeResource(resources, R.drawable.vidabien)
        corazonLleno = vidasLlenas.scale(80, 80, false)

        val vidasVacias = BitmapFactory.decodeResource(resources, R.drawable.vidamal)
        corazonVacio = vidasVacias.scale(80, 80, false)


        // imagenes cuenta atras
        val altoNum = 300
        val anchoNum = 200

        img1 =
            BitmapFactory.decodeResource(resources, R.drawable.uno).scale(anchoNum, altoNum, false)

        img2 =
            BitmapFactory.decodeResource(resources, R.drawable.dos).scale(anchoNum, altoNum, false)

        img3 =
            BitmapFactory.decodeResource(resources, R.drawable.tres).scale(anchoNum, altoNum, false)

    }

    // cargar fondos bien

    private fun cargarFondo(resId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, resId)
    }

    // para saber cuando poner en funcionamiento el juego
    fun resume() {
        jugando = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    // Controlar para no gastar bateria si el usuario sale del juego
    fun pause() {
        jugando = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        // quitar negrooooooo
        while (jugando && !surfaceHolder.surface.isValid) {
            try { Thread.sleep(10) } catch (e: Exception) {}
        }

        if (jugando) {
            draw()
        }
        cargarRecursos()
        cargandoRecursos = false

        while (jugando) {
            update()
            draw()
            control()
        }
    }

    private fun update() {
        // CUENTA ATRÁS
        if (enCuentaAtras) {
            val tiempoActual = System.currentTimeMillis()

            if (tiempoActual - tiempoCambioNumero > 1000) {
                numeroCuentaAtras--
                tiempoCambioNumero = tiempoActual // resetear el reloj


                if (numeroCuentaAtras <= 0) {
                    enCuentaAtras = false //quitar cuenta atrás
                    juegoIniciado = true  // empezar juego

                    tiempoAparicion = System.currentTimeMillis()
                    generarNuevoSlime()
                }
            }
            return
        }
        // para esperar a que el jugador ponga el nombre
        if (!juegoIniciado) return

        // Borrar la img del aplastado
        val tiempoActual = System.currentTimeMillis()
        listaManchas.removeAll { tiempoActual - it.tiempoGolpe > 1500 }

        // generar slimes
        if (slimeActual == null && width > 0 && height > 0) {
            generarNuevoSlime()
        }


        // comprobar tiempo que se tarda en pulsar
        if (slimeActual != null) {
            val tiempoActual = System.currentTimeMillis()

            if (tiempoActual - tiempoAparicion > tiempoLimite) {
                vidasActuales--
                soundPool?.play(sonidoPerderId, 1f, 1f, 0, 0, 1f)

                if (vidasActuales <= 0 && !gameOver) {
                    gameOver = true
                    val intent = Intent(context, GameOverActivity::class.java)
                    intent.putExtra("puntuacion", puntuacion)
                    intent.putExtra("nombreJugador", nombreJugador)
                    context.startActivity(intent)
                }
                generarNuevoSlime()
            }
        }
    }

    fun destruirSonidos() {
        soundPool?.release()
        soundPool = null
    }

    private fun control() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun iniciarPartida(nombre: String) {
        this.nombreJugador = nombre

        enCuentaAtras = true
        numeroCuentaAtras = 3
        tiempoCambioNumero = System.currentTimeMillis() // resetear reloj para que no me mate nada más empezar

        juegoIniciado = false

        soundPool?.play(sonidoCuentaAtras, 1f, 1f, 0, 0, 1f)
    }

    // Generar un slime y su posicion aleatoria
    private fun generarNuevoSlime() {
        val probabilidadEspecial = 5

        // probabilidad de que salga slime especial
        val esEspecial = (0..99).random() < probabilidadEspecial
        slimeActualEsEspecial = esEspecial

        if (esEspecial) {
            indiceSlimeActual = -1
            // SLIME ESPECIAL
            val bmp = BitmapFactory.decodeResource(resources, R.drawable.slimeespecialbien)
            slimeActual = bmp.scale(180, 150, false)
        } else {
            // SLIME NORMAL
            if (listaSlimesBien.isEmpty()) return

            // guardar indice
            indiceSlimeActual = (Math.random() * listaSlimesBien.size).toInt()
            slimeActual = listaSlimesBien[indiceSlimeActual]
        }

        // Posición aleatoria
        slimeActual?.let { bmp ->
            figuraX = (margenLateral + Math.random() * (width - bmp.width - margenLateral * 2)).toFloat()
            figuraY = (margenSuperior + Math.random() * (height - bmp.height - margenSuperior - margenLateral)).toFloat()
        }

        tiempoAparicion = System.currentTimeMillis()
    }

    private fun draw() {
        if (gameOver) return   // no dibujar nada si el juego terminó

            if (surfaceHolder.surface.isValid) {
                val canvas: Canvas = surfaceHolder.lockCanvas()
                val pantallaCompleta = android.graphics.Rect(0, 0, width, height)

                if (imagenFondo != null) {
                    canvas.drawBitmap(imagenFondo!!, null, pantallaCompleta, null)
                }
                val fondoVidas = when (vidasActuales) {
                    4 -> fondo4Vidas
                    3 -> fondo3Vidas
                    2 -> fondo2Vidas
                    1 -> fondo1Vida
                    else -> null
                }
                fondoVidas?.let {
                    canvas.drawBitmap(it, null, pantallaCompleta, null)
                }

                // texto puntuación
                paint.apply {
                    color = Color.parseColor("#272727")
                    textSize = 50f
                    isAntiAlias = true
                    typeface = fuentePuntos
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

                // texto puntuación
                paint.textAlign = Paint.Align.RIGHT

                canvas.drawText(
                    "$puntuacion",
                    width - 60f,   // margen derecho
                    150f,          // top
                    paint
                )

                paint.textAlign = Paint.Align.LEFT

                // dibujar cuenta atras
                if (enCuentaAtras) {
                    val imagenADibujar = when (numeroCuentaAtras) {
                        3 -> img3
                        2 -> img2
                        1 -> img1
                        else -> null
                    }

            imagenADibujar?.let { bmp ->
                // centrar imagen
                val xCentro = (width / 2f) - (bmp.width / 2f)
                val yCentro = (height / 2f) - (bmp.height / 2f)

                canvas.drawBitmap(bmp, xCentro, yCentro, null)
            }

            surfaceHolder.unlockCanvasAndPost(canvas)
            return
        }
                /// slimes aplastados
                for (mancha in listaManchas) {
                    canvas.drawBitmap(mancha.bitmap, mancha.x, mancha.y, null)
                }

                // slime vivo
                slimeActual?.let { bitmapVivo ->
                    canvas.drawBitmap(bitmapVivo, figuraX, figuraY, null)
                }

                surfaceHolder.unlockCanvasAndPost(canvas)
            }
    }

    // click en la pantalla
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!juegoIniciado) return true

        if (event.action == MotionEvent.ACTION_DOWN) {
            val dedoX = event.x
            val dedoY = event.y

            slimeActual?.let { bitmap ->
                // Comprobar click dedo
                if (dedoX >= figuraX && dedoX <= (figuraX + bitmap.width) &&
                    dedoY >= figuraY && dedoY <= (figuraY + bitmap.height)) {

                    val tiempoRestante = tiempoLimite - (System.currentTimeMillis() - tiempoAparicion)

                    //Puntos = Base + (TiempoRestante * Multiplicador)
                    val puntosBase = if (slimeActualEsEspecial) 50 else 10
                    val multiplicador = 0.05f

                    puntosPorAcierto = (puntosBase + (tiempoRestante * multiplicador)).toInt()

                    puntuacion += puntosPorAcierto

                    if (slimeActualEsEspecial) {
                        soundPool?.play(sonidoEspecialId, 1f, 1f, 0, 0, 1f)
                    }

                    // crear aplastado y añadir a lista
                    val tiempoGolpeActual = System.currentTimeMillis()
                    var bmpAplastado: Bitmap? = null

                    if (slimeActualEsEspecial) {
                        val bmp = BitmapFactory.decodeResource(resources, R.drawable.slimeespecialmal)
                        bmpAplastado = bmp.scale(180, 150, false)
                    } else {
                        // Buscar mismo slime que estaba bien, pero en la lista mal
                        if (indiceSlimeActual != -1 && indiceSlimeActual < listaSlimesMal.size) {
                            bmpAplastado = listaSlimesMal[indiceSlimeActual]
                        }
                    }

                    // guardar imagen aplastadaen la lista
                    if (bmpAplastado != null) {
                        listaManchas.add(ManchaSlime(bmpAplastado, figuraX, figuraY, tiempoGolpeActual))
                    }

                    contadorAciertos++

                    // aumentar dificultad
                    if (contadorAciertos >= aciertosSubirNivel) {
                        // Reducir tiempo
                        if (tiempoLimite > tiempoMinimo) {
                            tiempoLimite -= reduccionTiempo
                            contadorAciertos = 0
                        }
                    }
                    generarNuevoSlime()
                }
            }
        }
        return true
    }
}