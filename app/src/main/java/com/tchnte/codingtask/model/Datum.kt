package com.tchnte.codingtask.model

import com.google.gson.annotations.SerializedName

class Datum {
    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

}