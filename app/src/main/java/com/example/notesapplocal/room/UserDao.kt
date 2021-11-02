package com.example.notesapplocal.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT * FROM user_table")
    fun getAllUser(): List<User>

    @Query("SELECT * FROM user_table WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query(
        "SELECT * FROM user_table WHERE first_name LIKE :first AND " +
                "last_name LIKE :last LIMIT 1"
    )
    fun findByName(first: String, last: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: ArrayList<User>)

    @Delete
    fun delete(user: User)

    @Query(
        "SELECT * FROM user_table WHERE uid IS (:idOfSelection)"
    )
    fun findByUid(idOfSelection: Int):User
}