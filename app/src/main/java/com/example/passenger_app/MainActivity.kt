package com.example.passenger_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

private const val PERMISSION_REQUEST = 10
class MainActivity : AppCompatActivity() {
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
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
                    val intent = Intent(this,Login::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    true

                }
                else -> false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                getLocation()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            getLocation()
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val uid = Firebase.auth.currentUser?.uid
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationGps = p0
                            if (uid != null) {
                                Firebase.firestore.collection("Passengers").document(uid).update("Longitude",
                                    locationGps!!.longitude,"Latitude", locationGps!!.latitude)
                                    .addOnSuccessListener {
                                        Snackbar.make(button3, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Snackbar.make(button3, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationNetwork = p0
                            if (uid != null) {
                                Firebase.firestore.collection("Passengers").document(uid).update("Longitude",
                                    locationNetwork!!.longitude,"Latitude", locationNetwork!!.latitude)
                                    .addOnSuccessListener {
                                        Snackbar.make(button3, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Snackbar.make(button3, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    if (uid != null) {
                        Firebase.firestore.collection("Passengers").document(uid).update("Longitude",
                            locationGps!!.longitude,"Latitude", locationGps!!.latitude)
                            .addOnSuccessListener {
                                Snackbar.make(button3, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Snackbar.make(button3, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                            }
                    }
                }else{
                    if (uid != null) {
                        Firebase.firestore.collection("Passengers").document(uid).update("Longitude",
                            locationNetwork!!.longitude,"Latitude", locationNetwork!!.latitude)
                            .addOnSuccessListener {
                                Snackbar.make(button3, "Location Data feeds start", Snackbar.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Snackbar.make(button3, "Failed location feed", Snackbar.LENGTH_SHORT).show()
                            }
                    }
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                getLocation()

        }
    }
}
