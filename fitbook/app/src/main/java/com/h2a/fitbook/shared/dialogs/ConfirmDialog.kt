package com.h2a.fitbook.shared.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.h2a.fitbook.R

class ConfirmDialog(private val title: String, private val description: String?) :
    DialogFragment() {
    var onButtonClick: ((Boolean) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setMessage(description)
            builder.setPositiveButton(resources.getString(R.string.confirm_dialog_ok)) { dialog, _ ->
                onButtonClick?.invoke(true)
                dialog.cancel()
            }

            builder.setNegativeButton(resources.getString(R.string.confirm_dialog_cancel)) { dialog, _ ->
                onButtonClick?.invoke(false)
                dialog.cancel()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}