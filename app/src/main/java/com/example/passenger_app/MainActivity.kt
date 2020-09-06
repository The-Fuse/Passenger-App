package com.example.passenger_app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
        val user = Firebase.auth.currentUser
        if (user != null){
           //ljdfl
        }else {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        val db = Firebase.firestore
        val uid = user?.uid.toString()
        val ref = db.collection("Passengers").document(uid)
        ref.get()
            .addOnSuccessListener { document ->
                if (document !=null){
                    val username = document.get("name")
                    supportActionBar?.setTitle("Hello $username")
                }
            }
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.rewards -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.profile -> {
                    true

                }
                else -> false
            }
        }
    }
}