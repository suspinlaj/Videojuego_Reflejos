package com.example.videojuego

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Clase Kotlin para la animación de la portada
class AnimacionNubes(
    // Dónde se ponen las imágenes (FrameLayout en el XML)
    private val container: ViewGroup,
    // Define cuánto tiempo puede vivir la animación
    // asegura que la animación no siga corriendo si la pantalla ya no existe.
    private val scope: CoroutineScope,
    // Las imágenes que voy a usar
    private val imagenes: List<Int>

    // Permite que saber cuando la pantalla se enciendo o apaga (owner)
) : DefaultLifecycleObserver {

    // Para parar la animación manualmente
    private var generatorJob: Job? = null

    //Tamaño fijo de la imagen guardado en memoria para que la animación vaya
    // más fluida y no gaste batería calculando lo mismo mil veces.
    private var itemPixelSize: Int = 0

    // Asegurar que itemPixelSize tenga un valor válido sí o sí
    init {
        // Calcular tamaño una sola vez al iniciar la clase
        val context = container.context
        try {
            itemPixelSize = context.resources.getDimensionPixelSize(R.dimen.item_size)
        } catch (e: Exception) {
            itemPixelSize = (80 * context.resources.displayMetrics.density).toInt()
        }
    }

    // Método para iniciar la animación
    fun start() {
        if (generatorJob?.isActive == true) return // Si ya está corriendo, no hace nada.

        // Arranca la animacion y guarda el control en la variable generatorJob
        // para que luego se pueda parar con stop()
        generatorJob = scope.launch {
            while (true) { // Bucle infinito
                delay(3500)  // MÁS O MENOS IMAGENES
                crearImagenAnimacion() // Crea una imagen
            }
        }
    }

    // Detiene el bucle de la animación
    fun stop() {
        generatorJob?.cancel()
    }

    // "owner" hace referencia a la MainActivity

    //Activa la creación de imágenes justo cuando se empieza a ver la pantalla.
    // Se dispara solo cuando la pantalla aparece por el owner
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        start()
    }

    // Se detiene el metodo cuando se deja de ver la aplicacion.
    // Se dispara solo cuando la pantalla se oculta por el owner
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        stop()
    }


    // METODO PARA CREAR LA IMAGEN
    // Crea una imagen nueva, la hace caer mientras se desvanece
    //  y la borra de la memoria automáticamente al terminar su recorrido.
    private fun crearImagenAnimacion() {
        // Verificaciones de seguridad
        if (container.width == 0 || container.height == 0 || itemPixelSize == 0) return

        val context = container.context

        // Crea la Vista
        val imageView = ImageView(context).apply {
            // Tamaño de la imagen
            layoutParams = FrameLayout.LayoutParams(itemPixelSize, itemPixelSize)

            // Imagen al azar
            setImageResource(imagenes.random())

            // Opacidad
            //alpha = 0.5f

            // Empieza fuera de la pantalla a la izquierda
            translationX = -itemPixelSize.toFloat()

            // Posición vertical aleatoria
            val posicionImagenRandom = Random.nextInt(container.height - itemPixelSize)
            translationY = posicionImagenRandom.toFloat()
        }

        // Añadir la imagen al contenedor
        container.addView(imageView)

        // Destino final: fuera de la pantalla a la derecha
        val endX = container.width.toFloat()

        // Duración aleatoria
        val duration = Random.nextLong(8000, 12000)

        // Animación horizontal
        val moveAnimator = ObjectAnimator.ofFloat(
            imageView,
            "translationX",
            endX
        ).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
        }

    // Ejecutar todo lo anterior
        // Ejecutar la animación
        AnimatorSet().apply {
            play(moveAnimator) // Ejecuta el movimiento horizontal
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}

                // Cuando la imagen termina de cruzar la pantalla, se borra
                override fun onAnimationEnd(animation: Animator) {
                    container.removeView(imageView)
                }
            })
            start()
        }
    }
}