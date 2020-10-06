package com.tchnte.codingtask.presenter

import com.tchnte.codingtask.model.UserDataResponseDO
import com.tchnte.codingtask.service.ApiClient
import com.tchnte.codingtask.service.ApiInterface
import com.tchnte.codingtask.views.MainActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserDataPresenter(var view: MainActivity) {

    fun getUserInfo(
        pageNum: Int
    ) {

        val apiService = ApiClient.client?.create(ApiInterface::class.java)

        apiService?.getUsersList(pageNum)?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<UserDataResponseDO> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    view.onFailure(e.message)
                }

                override fun onNext(userDataResponseDO: UserDataResponseDO) {
                    view.onSuccess(userDataResponseDO)
                }
            })
    }

    interface UserDataPresenterInterface {

        fun onSuccess(userDataResponseDO: UserDataResponseDO)

        fun onFailure(message: String?)
    }
}



