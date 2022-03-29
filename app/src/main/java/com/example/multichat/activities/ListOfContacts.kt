package com.example.multichat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multichat.R
import com.example.multichat.adapters.UserAdapter
import com.example.multichat.models.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_of_contacts.*

class ListOfContacts : AppCompatActivity() {

    private var user = ""

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_contacts)

        intent.getStringExtra("user")?.let { user = it }

        initViews()
    }

    private fun initViews(){

        listUsersRecyclerView.layoutManager = LinearLayoutManager(this)
        listUsersRecyclerView.adapter =
            UserAdapter { user ->
                userSelected(user)
            }

        db.collection("users")
            .get()
            .addOnSuccessListener { users ->
                val listUsers = users.toObjects(Usuario::class.java)

                (listUsersRecyclerView.adapter as UserAdapter).setData(listUsers)
            }

        db.collection("users")
            .addSnapshotListener { users, error ->
                if(error == null){
                    users?.let {
                        val listUsers = it.toObjects(Usuario::class.java).sortedWith(compareBy { it.name })

                        (listUsersRecyclerView.adapter as UserAdapter).setData(listUsers)
                    }
                }
            }
    }

    private fun userSelected(user: Usuario) {
        //TODO: Asdasdasd
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