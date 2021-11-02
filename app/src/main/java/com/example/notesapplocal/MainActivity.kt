package com.example.notesapplocal

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefFile:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatButton>(R.id.btnLogin).setOnClickListener { onClick(it) }
        sharedPrefFile = resources.getString(R.string.app_name)
        sharedPreferences = this.getSharedPreferences(sharedPrefFile,  MODE_PRIVATE)
    }

    private fun onClick(v: View) {
        when(v.id){

            R.id.btnLogin->{
                validateFieldsAndLogin()
            }
            else-> Toast.makeText(this, "no click detected", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("SpellCheckingInspection")
    @SuppressLint("ApplySharedPref")
    private fun validateFieldsAndLogin() {
        val nameAcet = findViewById<AppCompatEditText>(R.id.userName)
        val passwordAcet = findViewById<AppCompatEditText>(R.id.password)
        val name:String = nameAcet.text.toString()
        val password:String = passwordAcet.text.toString()
        if (sharedPreferences.contains(name) && sharedPreferences.getString(name, "") != password){
            Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show()
            return
        }
        sharedPreferences.edit().putString(name, password).commit()
        val sf:SharedPreferences = getPreferences(MODE_PRIVATE)
        sf.edit().putString(name, password).commit()
        nameAcet.text?.clear()
        passwordAcet.text?.clear()
        startActivity(Intent(this, HomeActivity::class.java))
    }
}