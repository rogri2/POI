package com.example.multichat.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multichat.adapters.MessageAdapter
import com.example.multichat.R
import com.example.multichat.models.Message
import com.example.multichat.models.Usuario
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.messageTextField
import kotlinx.android.synthetic.main.activity_chat.messagesRecylerView
import kotlinx.android.synthetic.main.activity_chat.sendLocationButton
import kotlinx.android.synthetic.main.activity_chat.sendMessageButton

class Chatpublico : AppCompatActivity() {

    private var user = ""
    private var carrera: String = ""

    private var latitud = ""
    private var longitud = ""

    private var db = Firebase.firestore

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatpublico)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        intent.getStringExtra("user")?.let { user = it }

        val userRef = db.collection("users").document(user)

        userRef.get().addOnSuccessListener { documentSnapshot ->
            val userData = documentSnapshot.toObject<Usuario>()
            if (userData != null) {
                when (userData.carrera) {
                    "LMAD" -> carrera = "MbPEig1gis7HkK2FaI8B"
                    "LCC" -> carrera = "dNgfy8OFpXFFx0vGib8O"
                    "LSTI" -> carrera = "wWvPWywwmp8FRF1lYdX9"
                    "LF" -> carrera = "cQcte9A0WdSEi0igb47D"
                    "LA" -> carrera = "m5eGootcTLvfMBYLUJXV"
                    "LM" -> carrera = "rWnFSCLMI4OzCJbEyOkE"
                }
            }
            if(user.isNotEmpty()) {
                initViews()
            }
        }
    }

    private fun fetchLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                //Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()
                val message = Message(
                    //message = "Te comparto mi ubicación! https://maps.google.com/?q=${it.latitude},${it.longitude}",
                    message = "Te comparto mi ubicación! Latitud: ${it.latitude}, Longitud: ${it.longitude}",
                    from = user
                )
                db.collection("chatPublico").document(carrera).collection("publicMessages").document().set(message)

                messageTextField.setText("")
            }
        }
    }

    private fun initViews(){

        messagesRecylerView.layoutManager = LinearLayoutManager(this)
        messagesRecylerView.adapter = MessageAdapter(user)

        sendMessageButton.setOnClickListener { sendMessage() }
        sendLocationButton.setOnClickListener { sendLocation() }

        val chatRef = db.collection("chatPublico").document(carrera)

        chatRef.collection("publicMessages").orderBy("dob", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { messages ->
                val listMessages = messages.toObjects(Message::class.java)
                (messagesRecylerView.adapter as MessageAdapter).setData(listMessages)
            }

        chatRef.collection("publicMessages").orderBy("dob", Query.Direction.ASCENDING)
            .addSnapshotListener { messages, error ->
                if(error == null){
                    messages?.let {
                        val listMessages = it.toObjects(Message::class.java)
                        (messagesRecylerView.adapter as MessageAdapter).setData(listMessages)
                    }
                }
            }
    }

    private fun sendMessage(){
        val message = Message(
            message = messageTextField.text.toString(),
            from = user
        )

        db.collection("chatPublico").document(carrera).collection("publicMessages").document().set(message)

        messageTextField.setText("")

    }

    private fun sendLocation() {
        fetchLocation()
        /*val message = Message(
            message = "Te comparto mi ubicación! https://maps.google.com/?q=${latitud},${longitud}",
            from = user
        )
        db.collection("chats").document(chatId).collection("messages").document().set(message)

        messageTextField.setText("")*/
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