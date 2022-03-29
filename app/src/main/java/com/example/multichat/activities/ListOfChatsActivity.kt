package com.example.multichat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multichat.models.Chat
import com.example.multichat.adapters.ChatAdapter
import com.example.multichat.R
import com.example.multichat.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_of_chats.*
import java.util.*

class ListOfChatsActivity : AppCompatActivity() {
    private var user = ""

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_chats)

        intent.getStringExtra("user")?.let { user = it }

        if (user.isNotEmpty()){
            initViews()
        }
    }

    private fun initViews(){
        newChatButton.setOnClickListener { newChat() }
        logOutButton.setOnClickListener{ logOut() }

        listChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        listChatsRecyclerView.adapter =
            ChatAdapter { chat ->
                chatSelected(chat)
            }

        val userRef = db.collection("users").document(user)

        userRef.collection("chats")
            .get()
            .addOnSuccessListener { chats ->
                val listChats = chats.toObjects(Chat::class.java)

                (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
            }

        userRef.collection("chats")
            .addSnapshotListener { chats, error ->
                if(error == null){
                    chats?.let {
                        val listChats = it.toObjects(Chat::class.java)

                        (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
                    }
                }
            }
    }

    private fun chatSelected(chat: Chat){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    private fun newChat(){
        val chatId = UUID.randomUUID().toString()
        val otherUser = newChatText.text.toString()
        val users = listOf(user, otherUser)

        val otherUserAux = db.collection("users").document(otherUser)
        otherUserAux.get().addOnSuccessListener { documentSnapshot ->
            val otherUserData = documentSnapshot.toObject<Usuario>()

            if (otherUserData != null) {
                val chat = Chat(
                    id = chatId,
                    name = "Chat with ${otherUserData.name}",
                    users = users
                )

                db.collection("chats").document(chatId).set(chat)
                db.collection("users").document(user).collection("chats").document(chatId).set(chat)
                db.collection("users").document(otherUser).collection("chats").document(chatId).set(chat)

                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("chatId", chatId)
                intent.putExtra("user", user)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "No se pudo crear el chat", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun logOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        db.collection("users").document(user).update("status", "Offline")
    }

    override fun onResume() {
        super.onResume()
        db.collection("users").document(user).update("status", "Online")
    }

}