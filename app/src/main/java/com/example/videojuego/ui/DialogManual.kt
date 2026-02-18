package com.example.videojuego.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.videojuego.R

class DialogManual : DialogFragment() {

    override fun onStart() {
        super.onStart()
        val window = dialog?.window

        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        //oscurecer el fondo al abrir el dialog
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setDimAmount(0.8f)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())

        dialog.setContentView(R.layout.dialog_manual)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // poner colores a los numeros del texto
        val tvInstrucciones = dialog.findViewById<TextView>(R.id.tvInstrucciones)
        tvInstrucciones?.text = android.text.Html.fromHtml(getString(R.string.texto_instrucciones), android.text.Html.FROM_HTML_MODE_LEGACY)


        dialog.findViewById<View>(R.id.btnCerrar)?.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}

