package com.cha.firebaseapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cha.firebaseapp.R
import com.cha.firebaseapp.data.local.entity.AddUser
import com.cha.firebaseapp.data.local.models.Employee
import com.cha.firebaseapp.model.interfaces.HomeListener

class UsersAdapter(var userList: List<AddUser>, private var clickItem: HomeListener):
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    class UsersViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvNameID    : TextView = view.findViewById(R.id.tv_userName)
        var tvLatitude  : TextView = view.findViewById(R.id.tv_latitude)
        var tvLongitude : TextView = view.findViewById(R.id.tv_longitude)
        var tvEmail     : TextView = view.findViewById(R.id.tv_email)

        fun initialize( item: AddUser, action: HomeListener ) {
            tvNameID.text    = item.idUser + " - " + item.name
            tvLatitude.text  = item.latUser
            tvLongitude.text = item.lngUser
            tvEmail.text     = item.emailUser

            itemView.setOnClickListener {
                action.userClicked(item, adapterPosition)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UsersViewHolder( inflater.inflate(R.layout.recyclerview_user, parent, false) )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.initialize(userList[position], clickItem)
    }
}