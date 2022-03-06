package com.bugs.posisiin00.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.bugs.posisiin00.R

class SimpanDialog(private val activity: TambahSampelActivity) : DialogFragment(), View.OnClickListener {

    private lateinit var btnSimpan : Button
    private lateinit var btnBatal: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.rounded_corner)
        val view = inflater.inflate(R.layout.tambah_dialog, container, false)
        btnSimpan = view.findViewById(R.id.btn_simpan)
        btnBatal = view.findViewById(R.id.btn_batal)

        btnSimpan.setOnClickListener(this)
        btnBatal.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.btn_simpan -> {
                activity.simpanData()
                dismiss()
            }

            R.id.btn_batal -> {
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}