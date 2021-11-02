package com.example.notesapplocal

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapplocal.room.UsersListActivity
import com.example.notesapplocal.sqlite.UserActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*

class HomeActivity : AppCompatActivity() {

    private val fileName: EditText by lazy{ findViewById(R.id.editTextFile)}
    private val fileData: EditText by lazy{ findViewById(R.id.editTextData)}
    private val saveButton: Button by lazy{ findViewById(R.id.button_save)}
    private val viewButton: Button by lazy{ findViewById(R.id.button_view)}
    private val filepath:String by lazy { resources.getString(R.string.app_name) }
    private val STORAGE_PERMISSION_1: Int = 121
    private val STORAGE_PERMISSION_2: Int = 122
    private var myExternalFile: File? = null
    private val isExternalStorageReadOnly: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
        }
    private val isExternalStorageAvailable: Boolean
        get() {
            val extStorageState = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == extStorageState
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        findViewById<FloatingActionButton>(R.id.fabLogout).setOnClickListener { onClick(it) }
        findViewById<FloatingActionButton>(R.id.fabUserActivity).setOnClickListener { onClick(it) }
        findViewById<FloatingActionButton>(R.id.fabUserListActivity).setOnClickListener { onClick(it) }
        onCreateLocal()
    }

    private fun checkPermissions(requestCode: Int): Boolean {
        return if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode
            )
            false
        } else
            true
    }

    private fun onClick(v: View) {
        when (v.id) {
            R.id.fabLogout -> startActivity(Intent(this, MainActivity::class.java))
            R.id.fabUserActivity->startActivity(Intent(this, UserActivity::class.java))
            R.id.fabUserListActivity->startActivity(Intent(this, UsersListActivity::class.java))
            else -> {
                Toast.makeText(this, "Click not detected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onCreateLocal() {

        saveButton.setOnClickListener {
            if(checkPermissions(STORAGE_PERMISSION_1))saveFile(fileName, fileData)
        }
        viewButton.setOnClickListener {
            if(checkPermissions(STORAGE_PERMISSION_2))viewFile(fileName)
        }

        if (!isExternalStorageAvailable || isExternalStorageReadOnly) {
            saveButton.isEnabled = false
        }
    }

    private fun viewFile(fileName: EditText) {
        myExternalFile = File(getExternalFilesDir(filepath), fileName.text.toString())

        val filename = fileName.text.toString()
        myExternalFile = File(getExternalFilesDir(filepath), filename)
        if (filename.trim() != "") {
            val fileInputStream = FileInputStream(myExternalFile)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String?
            while (run {
                    text = bufferedReader.readLine()
                    text
                } != null) {
                stringBuilder.append(text)
            }
            fileInputStream.close()
            //Displaying data on EditText
            Toast.makeText(applicationContext, stringBuilder.toString(), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveFile(fileName: EditText, fileData: EditText) {
        myExternalFile = File(getExternalFilesDir(filepath), fileName.text.toString())
        try {
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(fileData.text.toString().toByteArray())
            fileOutPutStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(applicationContext, "data save", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_1 && checkPermissions(STORAGE_PERMISSION_1)) saveFile(fileName, fileData)
        else if(requestCode == STORAGE_PERMISSION_2 && checkPermissions(STORAGE_PERMISSION_2)) viewFile(fileName)
    }
}