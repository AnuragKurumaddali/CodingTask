package com.tchnte.codingtask.views

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import com.tchnte.codingtask.R
import com.tchnte.codingtask.roomdb.UserDatabase
import com.tchnte.codingtask.roomdb.UserEntity
import kotlinx.android.synthetic.main.add_user_form.*
import java.text.SimpleDateFormat
import java.util.*

class AddNewUserActivity : AppCompatActivity() {

    var database: UserDatabase? = null
    var userDao: UserEntity? = null
    private var selGender = ""
    private var selStatus = ""
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_user_form)

        initializeDB()

        rg_Gender.setOnCheckedChangeListener { group, ID ->
            when (ID) {
                R.id.rbMale -> {

                    //If you Salected Male
                    selGender = "Male"
                }
                R.id.rbFemale -> {
                    //If you Salected Female
                    selGender = "Female"
                }
            }
        }

        rg_Status.setOnCheckedChangeListener { group, ID ->
            when (ID) {
                R.id.rbActive -> {

                    //If you Salected Male
                    selStatus = "Active"
                }
                R.id.rbInActive -> {
                    //If you Salected Female
                    selStatus = "Inactive"
                }
            }
        }

        btnSaveInDb.setOnClickListener {
            if(TextUtils.isEmpty(isInputValidated()))
                insertDetailsIntoDB()
            else
                showToast(isInputValidated())
        }
    }

    private fun initializeDB() {
        database = Room.databaseBuilder(this, UserDatabase::class.java, "MyDatabase")
            .allowMainThreadQueries().build()
    }

    private fun insertDetailsIntoDB() {
        Thread(Runnable {
            userDao =
                UserEntity(
                    (etId.text.toString()).toLong(),
                    etName.text.toString(),
                    etEmail.text.toString(),
                    selGender,
                    selStatus,
                    getTimeStamp(),
                    getTimeStamp()
                )

            var lsEntity =  database!!.getUserDao().getUserById(userDao!!.id)
            if (lsEntity.isEmpty()) {
                var effectedRowsCount = database!!.getUserDao().insert(userDao!!)
                if (!effectedRowsCount.equals(0))
                    showToast("Details Inserted Successfully")
                finish()
            } else {
                var effectedRowsCount = database!!.getUserDao().update(userDao!!)
                if (!effectedRowsCount.equals(0))
                    showToast("Details Updated Successfully")
                finish()
            }
        }).start()
    }

    private fun showToast(message: String) {
        runOnUiThread(Runnable {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun getTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        return sdf.format(Date())
    }

    private fun isInputValidated():String{
        var emptyStr = ""
        emptyStr = if(TextUtils.isEmpty(etId.text) && TextUtils.isEmpty(etName.text) && TextUtils.isEmpty(etEmail.text) && TextUtils.isEmpty(selGender) && TextUtils.isEmpty(selStatus))
            "Please Insert Data Into All fields"
        else if(TextUtils.isEmpty(etId.text))
            "Please enter UserId"
        else if(TextUtils.isEmpty(etName.text))
            "Please enter UserName"
        else if(TextUtils.isEmpty(etEmail.text))
            "Please enter EmailId"
        else if(TextUtils.isEmpty(selGender))
            "Please select Gender"
        else if(TextUtils.isEmpty(selStatus))
            "Please select Status"
        else  if (!etEmail.text.toString().trim().matches(emailPattern))
            "Please enter valid Email Address"
        else
            ""
        return emptyStr
    }

}