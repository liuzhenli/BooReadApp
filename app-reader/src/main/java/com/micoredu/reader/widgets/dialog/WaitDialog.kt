package com.micoredu.reader.widgets.dialog

import android.app.Dialog
import android.content.Context
import com.microedu.lib.reader.databinding.DialogWaitBinding


@Suppress("unused")
class WaitDialog(context: Context) : Dialog(context) {

    val binding = DialogWaitBinding.inflate(layoutInflater)

    init {
        setCanceledOnTouchOutside(false)
        setContentView(binding.root)
    }

    fun setText(text: String): WaitDialog {
        binding.tvMsg.text = text
        return this
    }

    fun setText(res: Int): WaitDialog {
        binding.tvMsg.setText(res)
        return this
    }

}