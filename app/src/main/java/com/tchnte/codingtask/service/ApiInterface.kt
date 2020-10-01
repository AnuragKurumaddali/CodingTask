package com.tchnte.codingtask.service


import com.tchnte.codingtask.model.UserDataResponseDO
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiInterface {

//    https://gorest.co.in/public-api/users?page=1

    @GET("public-api/users")
    fun getUsersList(@Header("page") page: Int): Observable<UserDataResponseDO>

}

