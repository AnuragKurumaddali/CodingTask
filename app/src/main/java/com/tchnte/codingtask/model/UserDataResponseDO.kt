package com.tchnte.codingtask.model

import com.google.gson.annotations.SerializedName

class UserDataResponseDO {
    @SerializedName("code")
    var code: Long? = null

    @SerializedName("data")
    var data: List<Datum>? = null

    @SerializedName("meta")
    var meta: Meta? = null

}