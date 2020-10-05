package com.tchnte.codingtask.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.tchnte.codingtask.R
import com.tchnte.codingtask.adapter.UserListAdapter
import com.tchnte.codingtask.customviews.CustomProgressDialog
import com.tchnte.codingtask.listener.ItemClickListener
import com.tchnte.codingtask.listener.PaginationListener
import com.tchnte.codingtask.model.UserDataResponseDO
import com.tchnte.codingtask.presenter.UserDataPresenter
import com.tchnte.codingtask.roomdb.UserDatabase
import com.tchnte.codingtask.roomdb.UserEntity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),UserDataPresenter.UserDataPresenterInterface {

    private var userDataPresenter: UserDataPresenter? = null
    private var userList: MutableList<UserEntity>? = null
    private var userAdapter: UserListAdapter? = null
    var database: UserDatabase? = null

    private var currentPage: Int = 1
    private var is_LastPage = false
    private var totalPage: Long = 0
    private var is_Loading = false
    private var page_Size: Long = 0
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeDB()
        showProgressBar()
        initControls()

        fab!!.setOnClickListener { view ->
            var intent = Intent(this, AddNewUserActivity::class.java)
            startActivity(intent)
        }

        rv_List!!.addOnScrollListener(object : PaginationListener(rv_List!!.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                is_Loading = true
                currentPage++
                userDataPresenter!!.getUserInfo(currentPage)
            }

            override val isLastPage: Boolean
                get() = is_LastPage

            override val isLoading: Boolean
                get() = is_Loading

            override var pageSize: Long
                get() = page_Size
                set(value) {page_Size}
        })

    }

    private fun initControls(){
        userList = ArrayList()
        userAdapter = UserListAdapter(userList!!, object : ItemClickListener {
            override fun OnItemClick(userEntity: UserEntity) {
                var intent = Intent(applicationContext, DetailsActivity::class.java)
                intent.putExtra("UserData",userEntity)
                startActivity(intent)
            }
        })
        rv_List!!.layoutManager = LinearLayoutManager(this)
        rv_List!!.adapter = userAdapter
        userDataPresenter = UserDataPresenter(this)
        userDataPresenter!!.getUserInfo(1)
    }

    override fun onSuccess(userDataResponseDO: UserDataResponseDO) {
        if (currentPage != 1) userAdapter!!.removeLoading()
        userAdapter!!.addItems(userDataResponseDO.data)
        prepareDatabaseEntity(userDataResponseDO.data!!)
        totalPage = userDataResponseDO.meta!!.pagination!!.pages!!
        page_Size = userDataResponseDO.meta!!.pagination!!.limit!!
        // check weather is last page or not
        if (currentPage < totalPage) {
            userAdapter!!.addLoading()
        } else {
            is_LastPage = true
        }
        is_Loading = false
        hideProgressBar()
        rv_List!!.visibility = View.VISIBLE
        tv_NoData!!.visibility = View.GONE
    }

    override fun onFailure(message: String?) {
        hideProgressBar()
        rv_List!!.visibility = View.GONE
        tv_NoData!!.visibility = View.VISIBLE
    }

    private fun initializeDB() {
        database = Room.databaseBuilder(this, UserDatabase::class.java, "MyDatabase")
            .allowMainThreadQueries().build()
    }

    private fun prepareDatabaseEntity(lsUserEntity: List<UserEntity>) {

        Thread(Runnable {
            for (userEntity in lsUserEntity) {
                var lsEntity =  database!!.getUserDao().getUserById(userEntity.id)
                if (lsEntity.isEmpty()) {
                    var effectedRowsCount = database!!.getUserDao().insert(userEntity)
                    if (!effectedRowsCount.equals(0))
                        Log.e("aaa","Inserted "+userEntity.id)

                } else {
                    var effectedRowsCount = database!!.getUserDao().update(userEntity)
                    if (!effectedRowsCount.equals(0))
                        Log.e("aaa","Updated "+userEntity.id)
                }
            }
        }).start()
    }

    fun showProgressBar(){
        progressDialog.show(this,"Please Wait...")
    }

    fun hideProgressBar(){
        progressDialog.dialog.dismiss()
    }

}