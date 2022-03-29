package com.example.multichat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.multichat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_principal.*

class Principal : AppCompatActivity() {

    private var user = ""

    private val auth = Firebase.auth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        intent.getStringExtra("user")?.let { user = it }

        idChat.setOnClickListener { GoToChats() }
        idGroup.setOnClickListener { GoToGroup() }
        idUsuarios.setOnClickListener { GoToUsuarios() }

        idSignout.setOnClickListener { signOut() }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        db.collection("user").document(user).update("status", "Offline")
        val intent = Intent(this, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finish()

    }

    private fun GoToChats() {
        val currentUser = auth.currentUser

        val intent = Intent(this, ListOfChatsActivity::class.java)
        if (currentUser != null) {
            intent.putExtra("user", currentUser.email)
        }
        startActivity(intent)

    }

    private fun GoToGroup() {
        val currentUser = auth.currentUser

        val intent = Intent(this, Chatpublico::class.java)
        if (currentUser != null) {
            intent.putExtra("user", currentUser.email)
        }
        startActivity(intent)

    }

    private fun GoToUsuarios() {
        val currentUser = auth.currentUser

        val intent = Intent(this, ListOfContacts::class.java)
        if (currentUser != null) {
            intent.putExtra("user", currentUser.email)
        }
        startActivity(intent)

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

