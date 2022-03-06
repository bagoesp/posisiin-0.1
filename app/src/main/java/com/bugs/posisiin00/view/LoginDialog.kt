package com.bugs.posisiin00.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bugs.posisiin00.R
import com.google.firebase.auth.FirebaseAuth

class LoginDialog: DialogFragment(), View.OnClickListener {

    private lateinit var edtEmail: EditText
    private lateinit var edtPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnBatal: Button

    private lateinit var pbDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.rounded_corner)
        val view = inflater.inflate(R.layout.login_dialog, container, false)
        edtEmail = view.findViewById(R.id.et_email)
        edtPass = view.findViewById(R.id.et_password)
        btnLogin = view.findViewById(R.id.btn_login)
        btnBatal = view.findViewById(R.id.btn_cancel)

        btnLogin.setOnClickListener(this)
        btnBatal.setOnClickListener(this)

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
            R.id.btn_login -> {
                autentikasi()
            }

            R.id.btn_cancel -> {
                dismiss()
            }
        }
    }

    private fun autentikasi(){
        showProgressBar()

        val email = edtEmail.text.toString().trim() { it <= ' ' }
        val password = edtPass.text.toString().trim() { it <= ' ' }

        // login using firebaseauth
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    hideProgressBar()
                    dismiss()
                    val intent = Intent(context, KelolaDataActivity::class.java)
                    startActivity(intent)
                } else {
                    hideProgressBar()
                    Toast.makeText(context, "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showProgressBar() {
        pbDialog = Dialog(requireContext())
        pbDialog.setContentView(R.layout.dialog_progress)
        pbDialog.setCancelable(false)
        pbDialog.setCanceledOnTouchOutside(false)

        pbDialog.show()
    }

    private fun hideProgressBar() {
        pbDialog.dismiss()
    }
}