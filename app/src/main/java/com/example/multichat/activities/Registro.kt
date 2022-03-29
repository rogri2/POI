package com.example.multichat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.multichat.R
import com.google.firebase.auth.ktx.auth
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.multichat.models.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.createButton
import kotlinx.android.synthetic.main.activity_registro.*

class Registro : AppCompatActivity() {

    private val auth = Firebase.auth
    private var db = Firebase.firestore
    private var carrera = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val spinner = findViewById<Spinner>(R.id.spn_carreras)
        val carreras = resources.getStringArray(R.array.carreras)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, carreras)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                carrera = carreras[pos]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        createButton.setOnClickListener { createUser() }
        idCancelar.setOnClickListener { RegistroBack() }
    }
    private fun createUser(){
        val user = Usuario(
            name = idNombre.text.toString(),
            correo = idCorreoRegistro.text.toString(),
            pass = idContraRegistro.text.toString(),
            carrera = carrera,
            status = "Offline"
        )

        if (user.name.isNotEmpty() && user.correo.isNotEmpty() && user.pass.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(user.correo, user.pass).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    db.collection("users").document(user.correo).set(user)
                    Toast.makeText(applicationContext,"User created. Logging in...", Toast.LENGTH_LONG).show();
                    checkUser()
                } else {
                    task.exception?.let {
                        Toast.makeText(baseContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
        else {
            Toast.makeText(applicationContext,"All fields required",Toast.LENGTH_LONG).show();
        }

    }

    private fun checkUser(){
        val currentUser = auth.currentUser

        if(currentUser != null){
            db.collection("user").document(currentUser.email!!).update("status", "Online")
            val intent = Intent(this, Principal::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)

            finish()
        }
    }

    private fun RegistroBack(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
