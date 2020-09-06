package com.example.passenger_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        registrationtransfer.setOnClickListener {
            val intent = Intent(this,Registration::class.java)
            startActivity(intent)
            finish()
        }
        button.setOnClickListener {
            login()
        }
    }
    private fun login(){
        val email = login_email.text.toString()
        val password = login_password.text.toString()
        if (email.isEmpty() || password.isEmpty()){
            Snackbar.make(button, "Please Fill out the field!!", Snackbar.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                Snackbar.make(button, "Logged in Successfully !!", Snackbar.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)
                finish()
            }
    }
}