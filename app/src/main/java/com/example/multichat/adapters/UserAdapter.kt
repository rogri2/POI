package com.example.multichat.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.multichat.R
import com.example.multichat.models.Usuario
import kotlinx.android.synthetic.main.item_chat.view.*
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter(val chatClick: (Usuario) -> Unit): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var users: List<Usuario> = emptyList()

    fun setData(list: List<Usuario>) {
        users = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserAdapter.UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val aux: String = users[position].name + " - " + users[position].correo
        holder.itemView.userNameText.text = aux
        val status = users[position].status
        holder.itemView.userStatusView.text = status
        when (status) {
            "Offline" -> holder.itemView.userStatusView.setTextColor(Color.RED)
            "Online" -> holder.itemView.userStatusView.setTextColor(Color.GREEN)
        }

        holder.itemView.setOnClickListener {
            chatClick(users[position])
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}