package com.tchnte.codingtask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tchnte.codingtask.R
import com.tchnte.codingtask.holder.BaseViewHolder
import com.tchnte.codingtask.listener.ItemClickListener
import com.tchnte.codingtask.roomdb.UserEntity

class UserListAdapter(lsUserEntitys: MutableList<UserEntity>?, itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoaderVisible = false
    private val mUserEntity: MutableList<UserEntity>? = lsUserEntitys
    private val itemListener: ItemClickListener? = itemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.user_list_cell, parent, false)
            )
            VIEW_TYPE_LOADING -> ProgressHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.progress_loading, parent, false)
            )
            else ->
                ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.user_list_cell, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == mUserEntity?.size!! - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return mUserEntity?.size ?: 0
    }

    fun addItems(mUserEntity: List<UserEntity>?) {
        this.mUserEntity?.addAll(mUserEntity!!)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        mUserEntity?.add(UserEntity(0,"","","","","",""))
        notifyItemInserted(mUserEntity?.size!! - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = mUserEntity?.size!! - 1
        val item: UserEntity? = getItem(position)
        if (item != null) {
            mUserEntity.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        mUserEntity?.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): UserEntity? {
        return if(mUserEntity != null && mUserEntity.size > position)
            mUserEntity[position]
        else
            null
    }

    inner class ViewHolder internal constructor(itemView: View?) :
        BaseViewHolder(itemView!!) {
        var tv_Id: TextView? = null
        var tv_Status: TextView? = null
        var tv_UserName: TextView? = null
        var tv_UserEmail: TextView? = null
        var tv_Gender: TextView? = null
        var ll_listItem: LinearLayout? = null
        override fun clear() {}
        override fun onBind(position: Int?) {
            super.onBind(position)
            val item: UserEntity? = mUserEntity?.get(position!!)
            tv_Id?.text = item?.id.toString()
            tv_Status?.text = item?.status
            tv_UserName?.text = item?.name
            tv_UserEmail?.text = item?.email
            tv_Gender?.text = item?.gender

            ll_listItem?.setOnClickListener {
                itemListener?.OnItemClick(item!!)
            }
        }

        init {
            tv_Id = itemView?.findViewById(R.id.tv_Id)
            tv_Status = itemView?.findViewById(R.id.tv_Status)
            tv_UserName = itemView?.findViewById(R.id.tv_UserName)
            tv_UserEmail = itemView?.findViewById(R.id.tv_UserEmail)
            tv_Gender = itemView?.findViewById(R.id.tv_Gender)
            ll_listItem = itemView?.findViewById(R.id.ll_listItem)
        }
    }

    inner class ProgressHolder internal constructor(itemView: View?) :
        BaseViewHolder(itemView) {
        override fun clear() {}


    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
    }

}