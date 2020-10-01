package com.tchnte.codingtask.views

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.tchnte.codingtask.R
import com.tchnte.codingtask.model.Datum
import com.tchnte.codingtask.model.UserDataResponseDO
import com.tchnte.codingtask.presenter.UserDataPresenter

class MainActivity : AppCompatActivity(),UserDataPresenter.UserDataPresenterInterface {

    private var userDataPresenter: UserDataPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        userDataPresenter = UserDataPresenter(this)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Toast.makeText(applicationContext,"Fab is clicked",Toast.LENGTH_SHORT).show()
        }

        userDataPresenter!!.getUserInfo(1);
    }

    override fun onSuccess(userDataResponseDO: UserDataResponseDO) {
        Log.e("aaa","Response "+userDataResponseDO.data)
    }

    override fun onFailure(message: String?) {
        Log.e("aaa","Response "+message)
    }
}