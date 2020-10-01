package com.tchnte.codingtask.model

import com.google.gson.annotations.SerializedName

class Pagination {
    @SerializedName("limit")
    var limit: Long? = null

    @SerializedName("page")
    var page: Long? = null

    @SerializedName("pages")
    var pages: Long? = null

    @SerializedName("total")
    var total: Long? = null

}