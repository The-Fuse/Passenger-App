package com.example.passenger_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.passenger_app.MainActivity
import com.example.passenger_app.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registration.*

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()
        logintransfer.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        registration.setOnClickListener {
            performRegister()
        }
    }

    private fun performRegister() {
        val email = regisration_email.text.toString()
        val password = registration_password.text.toString()
        val username = user_name.text.toString()
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Snackbar.make(registration, "Please Fill out all the Fields!!", Snackbar.LENGTH_SHORT)
                .show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Snackbar.make(registration, "Please try again !!", Snackbar.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                } else {
                    Snackbar.make(
                        registration,
                        "Account Created Succesfully!!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    saveUserToFirebaseDatabase()
                }
            }
    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val db = Firebase.firestore
        val user = User(user_name.text.toString(), uid)
        db.collection("Passengers").document(uid).set(user)
            .addOnSuccessListener {
                Log.d("Main", "details saved successfully in database : $(it.result.user.uid)")
            }

    }

}

class User(val name: String, val uid: String)