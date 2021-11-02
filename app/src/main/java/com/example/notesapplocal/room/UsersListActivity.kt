package com.example.notesapplocal.room

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplocal.R
import com.example.notesapplocal.sqlite.DatabaseHandler
import com.example.notesapplocal.sqlite.EmpModelClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class UsersListActivity : AppCompatActivity() {

    private lateinit var repository: Repository
    private lateinit var adapter: MyUserListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener { onClick(it) }
        findViewById<FloatingActionButton>(R.id.fabEdit).setOnClickListener { onClick(it) }
        findViewById<FloatingActionButton>(R.id.fabRemove).setOnClickListener { onClick(it) }
        repository = Repository(application)
        //add 3 users so make it reflect in the db
        val users: ArrayList<User> = arrayListOf()
        users.add(User(123, "Avishkar", "Yadav"))
        users.add(User(124, "Gauransh", "Yadav"))
        users.add(User(125, "Dakshesh", "Yadav"))
        adapter = MyUserListAdapter(users)
        runBlocking {
            repository.saveUsers(users)
            adapter = MyUserListAdapter(repository.getAllUser())
            initListView()
        }
        repository.getAllUsers().observe(this, {
            runOnUiThread {
                adapter = MyUserListAdapter(it)
                initListView()
            }
        })
    }

    private fun onClick(view: View?) {
        when (view?.id) {
            R.id.fabRemove -> {
                if (checkSelection()) showAlertBeforeRemove()
            }

            R.id.fabEdit -> {
                if (!checkSelection())return
                runBlocking {
                    showAlertBeforeEdit(repository.findByUid(adapter.getIdOfSelection(adapter.getSelectedPosition())))
                }
            }

            R.id.fabAdd -> {
                if(checkSelection())showAlertBeforeAdd()
            }
            else -> {
                Toast.makeText(this, "Invalid click detected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkSelection(): Boolean {
        return if (adapter.getSelectedPosition() ==-1){
            Toast.makeText(this, "Please select a record", Toast.LENGTH_SHORT).show()
            false
        } else true
    }

    private fun showAlertBeforeAdd() {
        val dialogBuilder = AlertDialog.Builder(this)
        val b by lazy { dialogBuilder.create() }
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_user_dialog, null)
        dialogBuilder.setView(dialogView)

        val edtId = dialogView.findViewById(R.id.updateUid) as EditText
        val edtFName = dialogView.findViewById(R.id.updateFirstName) as EditText
        val edtLName = dialogView.findViewById(R.id.updateLastName) as EditText

        dialogBuilder.setTitle("Add Record")
        dialogBuilder.setMessage("Enter data below")
        dialogBuilder.setPositiveButton("Add") { _, _ ->

            val updateId = edtId.text.toString()
            val updateFname = edtFName.text.toString()
            val updateLname = edtLName.text.toString()
            updateUser(User(updateId.toInt(), updateFname, updateLname))
            b.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
            //pass
            b.dismiss()
        }
        b.show()
    }

    private fun showAlertBeforeEdit(user: User) {
        val dialogBuilder = AlertDialog.Builder(this)
        val b by lazy { dialogBuilder.create() }
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_user_dialog, null)
        dialogBuilder.setView(dialogView)

        val edtId = dialogView.findViewById(R.id.updateUid) as EditText
        val edtFName = dialogView.findViewById(R.id.updateFirstName) as EditText
        val edtLName = dialogView.findViewById(R.id.updateLastName) as EditText

        edtId.setText(user.uid.toString())
        edtFName.setText(user.firstName)
        edtLName.setText(user.lastName)

        dialogBuilder.setTitle("Update Record")
        dialogBuilder.setMessage("Enter data below")
        dialogBuilder.setPositiveButton("Update") { _, _ ->

            val updateId = edtId.text.toString()
            val updateFname = edtFName.text.toString()
            val updateLname = edtLName.text.toString()
            updateUser(User(updateId.toInt(), updateFname, updateLname))
            b.dismiss()

        }
        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
            //pass
            b.dismiss()
        }
        b.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUser(user: User) {
        runBlocking {
            repository.saveUser(user)
            adapter.listUsers = repository.getAllUser()
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }

    private fun showAlertBeforeRemove() {
        //creating AlertDialog for taking user id
        val dialogBuilder = AlertDialog.Builder(this)
        val b by lazy { dialogBuilder.create() }
        dialogBuilder.setTitle("Delete Record")
        dialogBuilder.setPositiveButton("Delete") { _, _ ->
            removeUser()
            b.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
            //pass
            b.dismiss()
        }
        b.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeUser() {
        runBlocking {
            repository.delete(repository.findByUid(adapter.getIdOfSelection(adapter.getSelectedPosition())))
            adapter.listUsers = repository.getAllUser()
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initListView() {
        val recyclerView: RecyclerView = findViewById(R.id.rvList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()
    }
}