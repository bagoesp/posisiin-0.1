package com.bugs.posisiin00.view

import android.app.Dialog
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bugs.posisiin00.R
import com.bugs.posisiin00.databinding.DialogProgressBinding

open class BaseActivity : AppCompatActivity() {

    private lateinit var pbDialog: Dialog

    private lateinit var binding: DialogProgressBinding

    fun showProgressBar(info: String) {
        pbDialog = Dialog(this)
        binding = DialogProgressBinding.inflate(layoutInflater)
        pbDialog.setContentView(binding.root)
        pbDialog.setCancelable(false)
        pbDialog.setCanceledOnTouchOutside(false)

        binding.tvProgress.text = info

        pbDialog.show()
    }

    fun hideProgressBar() {
        pbDialog.dismiss()
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}