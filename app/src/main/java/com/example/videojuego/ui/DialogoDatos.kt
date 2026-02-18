package com.example.videojuego.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.example.videojuego.R
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class DialogoDatos : DialogFragment() {

    companion object {
        const val CLAVE_PETICION = "PeticionDialogoNombre"
        const val CLAVE_RESULTADO = "NombreUsuario"
    }


    override fun onStart() {
        super.onStart()
        val window = dialog?.window

        // hacer el dialog más pequeño o grande
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        //oscurecer el fondo al abrir el dialog
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setDimAmount(0.8f)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogo_personalizado)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val editText = dialog.findViewById<TextInputEditText>(R.id.edtNombre)

        dialog.findViewById<View>(R.id.btnCancelar)?.setOnClickListener {
            setFragmentResult(CLAVE_PETICION, bundleOf(CLAVE_RESULTADO to null))
            dismiss()
        }

        dialog.findViewById<View>(R.id.btnGuardar)?.setOnClickListener {
            val nombre = editText.text.toString()
            setFragmentResult(CLAVE_PETICION, bundleOf(CLAVE_RESULTADO to nombre))
            dismiss()        }

        return dialog
    }

}