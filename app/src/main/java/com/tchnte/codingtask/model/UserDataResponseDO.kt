package com.tchnte.codingtask.model

import com.google.gson.annotations.SerializedName
import com.tchnte.codingtask.roomdb.UserEntity

class UserDataResponseDO {
    @SerializedName("code")
    var code: Long? = null

    @SerializedName("data")
    var data: List<UserEntity>? = null

    @SerializedName("meta")
    var meta: Meta? = null

}