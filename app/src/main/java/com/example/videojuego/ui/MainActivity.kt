package com.example.videojuego.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.example.videojuego.R
import com.example.videojuego.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var respirarAnimacion: AnimatorSet? = null
    private var animacionNubes: AnimacionNubes? = null
    private var mediaPlayer: MediaPlayer? = null
    private val handlerMusica = Handler(Looper.getMainLooper())
    private val runnableMusica = Runnable { mediaPlayer?.start() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        animacionPortada()
        reproducirAudioInicio()
    }


    override fun onStart() {
        super.onStart()
        respirarAnimacion = animacionPersonaje(binding.imgPersonaje)
        //música al entrar o volver a la pantalla
        handlerMusica.postDelayed(runnableMusica, 1000)
    }

    // para que se pare la animación al cambiar la pantalla
    override fun onStop() {
        super.onStop()

        //cancelar temp musica
        handlerMusica.removeCallbacks(runnableMusica)

        respirarAnimacion?.cancel()
        respirarAnimacion = null

        // para que al volver, el personaje empiece de 0
        binding.imgPersonaje.apply {
            scaleX = 1f
            scaleY = 1f
            translationY = 0f
        }

        // parar musica
        mediaPlayer?.pause()
    }

    private fun animacionPersonaje(imageView: ImageView): AnimatorSet {

        // para mejorar el rendimiento
        imageView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // con esto anclo a los "pies" para que la animación crezca hacia arriba
        imageView.post {
            imageView.pivotX = imageView.width / 2f
            imageView.pivotY = imageView.height.toFloat()
        }

        // Estiramiento vertical (es un poco como el inhalar / exhalar)
        val stretchY = ObjectAnimator.ofFloat(
            imageView,
            View.SCALE_Y,
            1f,
            1.06f // para cambiar cuanto se estira para arriba
        ).apply {
            duration = 1500 // para cambiar la duración
            // con esto hace animacion ping-pong, infinito
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            // esto sirve para que empiece lento, acelera y vuelve a frenar
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Para que cuando el cuerpo se estire para arriba(Y), se estreche un poco(X)
        val squashX = ObjectAnimator.ofFloat(
            imageView,
            View.SCALE_X,
            1f,
            0.98f // // para cambiar cuanto se estrecha los lados
        ).apply {
            duration = 1800
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Micro desplazamiento hacia arriba
        val floatUp = ObjectAnimator.ofFloat(
            imageView,
            View.TRANSLATION_Y,
            0f,
            -2f // mueve la imagen 3 pixeles hacia arriba
        ).apply {
            duration = 1800
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Irregularidad para que no sea tan mecánico
        stretchY.startDelay = 120

        // agrupar las animaciones (Y,X, desplazamiento hacia arriba)
        return AnimatorSet().apply {
            playTogether(stretchY, squashX, floatUp)
            start()
        }
    }

    private fun animacionPortada() {

        val listaImagenes = listOf(
            R.drawable.nube1,
            R.drawable.nube2,
            R.drawable.nube3,
            R.drawable.nube4,
            R.drawable.nube5,
            R.drawable.nube6,
            R.drawable.nube7,
            R.drawable.nube8,
            R.drawable.nube9
        )

        animacionNubes = AnimacionNubes(
            container = binding.animacion,
            scope = lifecycleScope,
            imagenes = listaImagenes
        )

        // Conectar la animación al ciclo de vida
        lifecycle.addObserver(animacionNubes!!)
    }

    private fun reproducirAudioInicio() {
        mediaPlayer = MediaPlayer.create(this, R.raw.inicio)

        // repetir en bucle infinito
        mediaPlayer?.isLooping = true

        // ajutar el volumen a lo que quiero
        val volumen = 0.3f
        mediaPlayer?.setVolume(volumen, volumen)

    }

    //  parar música al salir
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun onClickVidas(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    fun onClickPuntuacion(view: View) {
        val intent = Intent(this, Puntuaciones::class.java)
        startActivity(intent)
    }

}