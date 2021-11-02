package com.example.notesapplocal.room

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(application: Application) {

    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "NotesApp")
        .allowMainThreadQueries().fallbackToDestructiveMigration().build()
    private val userDao by lazy { db.userDao() }


    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAll()
    }

    fun getAllUser(): List<User> {
        return userDao.getAllUser()
    }

    suspend fun saveUser(user: User) {
        return withContext(Dispatchers.IO) { userDao.insertAll(user) }
    }

    suspend fun saveUsers(users: ArrayList<User>) {
        return withContext(Dispatchers.IO) { userDao.insertAll(users) }
    }

    suspend fun findByName(first: String, last: String): User {
        return withContext(Dispatchers.IO) { userDao.findByName(first, last) }
    }

    suspend fun delete(user: User) {
        return withContext(Dispatchers.IO) { userDao.delete(user) }
    }

    suspend fun findByUid(idOfSelection: Int): User {
        return withContext(Dispatchers.IO) {
            userDao.findByUid(idOfSelection)
        }
    }
}