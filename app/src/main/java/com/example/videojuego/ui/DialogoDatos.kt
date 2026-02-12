package com.example.videojuego.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogoDatos : DialogFragment() {

    companion object {
        const val CLAVE_PETICION = "PeticionDialogoNombre"
        const val CLAVE_RESULTADO = "NombreUsuario"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val editText = EditText(requireContext())
        editText.hint = "Introduce tu nombre"

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Guardar nombre")
            .setView(editText)
            .setNegativeButton("Cancelar") { _, _ ->
                setFragmentResult(CLAVE_PETICION, bundleOf(CLAVE_RESULTADO to null))
            }
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = editText.text.toString()
                setFragmentResult(CLAVE_PETICION, bundleOf(CLAVE_RESULTADO to nombre))
            }
            .create()
    }
}