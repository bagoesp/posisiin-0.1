package com.bugs.posisiin00.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.bugs.posisiin00.R
import com.bugs.posisiin00.model.DataSampel

class HapusDialog(private val activity: KelolaDataActivity) : DialogFragment(), View.OnClickListener {

    private lateinit var btnHapus: Button
    private lateinit var btnBatal: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.rounded_corner)
        val view = inflater.inflate(R.layout.hapus_dialog, container, false)
        btnHapus = view.findViewById(R.id.btn_hapus)
        btnBatal = view.findViewById(R.id.btn_batal_hapus)

        btnHapus.setOnClickListener(this)
        btnBatal.setOnClickListener(this)

        val dataSampel = arguments

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            btnHapus.id -> {
                activity.deleteSelected()
                dismiss()
            }

            btnBatal.id -> {
                dismiss()
            }
        }
    }
}