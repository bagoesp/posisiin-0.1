package com.bugs.posisiin00.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.bugs.posisiin00.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var btnKelola: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKelolaData.setOnClickListener(this)
        binding.btnCariPosisi.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            binding.btnKelolaData.id -> {
                LoginDialog().show(supportFragmentManager, "LoginDialogFragment")
            }

            binding.btnCariPosisi.id -> {
                // val intent = Intent(this@MainActivity, OnlineActivity::class.java)
                val intent = Intent(this@MainActivity, MapActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}