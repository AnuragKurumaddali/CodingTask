package com.tchnte.codingtask.listener

import com.tchnte.codingtask.roomdb.UserEntity

interface ItemClickListener {
    fun OnItemClick(userEntity: UserEntity)
}