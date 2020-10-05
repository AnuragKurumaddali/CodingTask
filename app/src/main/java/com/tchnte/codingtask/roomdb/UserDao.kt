package com.tchnte.codingtask.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun insert(user: UserEntity): Long

    @Insert
    fun insertAll(lsUserEntity: List<UserEntity>)

    @Update
    fun update(user: UserEntity): Int

    @Delete
    fun delete(user: UserEntity)

    @Query("SELECT * FROM user")
    fun getAllUsers(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM user WHERE user_id = :id")
    fun getUserById(id: Long?): List<UserEntity>

}