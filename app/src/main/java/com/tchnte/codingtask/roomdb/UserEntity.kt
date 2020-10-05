package com.tchnte.codingtask.roomdb

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "user")
class UserEntity:Serializable {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_id")
    @SerializedName("id")
    @NonNull
    var id: Long? = null

    @ColumnInfo(name = "user_Name")
    @SerializedName("name")
    var name: String? = null

    @ColumnInfo(name = "user_email")
    @SerializedName("email")
    var email: String? = null

    @ColumnInfo(name = "gender")
    @SerializedName("gender")
    var gender: String? = null

    @ColumnInfo(name = "status")
    @SerializedName("status")
    var status: String? = null

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    var createdAt: String? = null

    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    var updatedAt: String? = null

    constructor(id: Long?, name: String, email: String, gender: String, status: String, createdAt: String, updatedAt: String) {
        this.id = id
        this.name = name
        this.email = email
        this.gender = gender
        this.status = status
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}