package com.mitsuru.insight

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mitsuru.insight.databinding.ActivitySignInBinding
import com.mitsuru.insight.util.User
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object{
        private const val REQUEST_CODE = 10
        private const val PERMISSION_LOCATION = 100
        private var latitude = 0f
        private var longitude = 0f
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            if (requestCode == PERMISSION_LOCATION){
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show()
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        supportActionBar?.hide()
        showLoading(false)
        startAnimation()

        binding.apply {
            btnRegister.setOnClickListener { startActivity(Intent(this@SignInActivity, SignUpActivity::class.java)) }
            btnLogin.setOnClickListener {
                binding.apply {
                    val email = edtEmail.text.toString()
                    val password = edtPasssword.text.toString()

                    if (email.isEmpty()){
                        edtEmail.error = "email tidak boleh kosong"
                        edtEmail.requestFocus()
                        return@setOnClickListener
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        edtEmail.error = "email invalid"
                        edtEmail.requestFocus()
                        return@setOnClickListener
                    }

                    if (password.isEmpty()){
                        edtPasssword.error = "password tidak boleh kosong"
                        edtPasssword.requestFocus()
                        return@setOnClickListener
                    }

                    if (password.length < 6){
                        edtPasssword.error = "password tidak boleh kurang dari 6 karakter"
                        edtPasssword.requestFocus()
                        return@setOnClickListener
                    }
                    LoginFirebase(email , password)
                    LocationToFirebase(email , latitude , longitude)
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (checkPermission()){
            if (isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task ->
                    val location: Location? = task.result
                    if (location==null){
                        Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Get success", Toast.LENGTH_SHORT).show()
                        latitude = location.latitude.toFloat()
                        longitude = location.longitude.toFloat()
                    }
                }
            }else{
                Toast.makeText(this, "Nyalakan Location", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =  getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_LOCATION
        )
    }

    private fun checkPermission(): Boolean{
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }

        return false
    }

    private fun LocationToFirebase(email: String , lat: Float, lon: Float){
        val ref = FirebaseDatabase.getInstance().getReference("user")

        val userId = ref.push().key

        val user = User(userId , email , lat, lon)

        if (userId != null){
            ref.child(userId).setValue(user).addOnCompleteListener {
                Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(
                        this,
                        "Login berhasil",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun startAnimation() {
        binding.apply {
            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -20f, 20f).apply {
                duration = 5000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val image = ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f).setDuration(500)
            val title = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f).setDuration(500)
            val email = ObjectAnimator.ofFloat(edtEmail, View.ALPHA, 1f).setDuration(500)
            val password = ObjectAnimator.ofFloat(edtPasssword, View.ALPHA, 1f).setDuration(500)
            val btnLogin = ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 1f).setDuration(500)
            val btnRegister = ObjectAnimator.ofFloat(btnRegister, View.ALPHA, 1f).setDuration(500)

            val together = AnimatorSet().apply {
                playTogether(btnLogin, btnRegister)
            }

            AnimatorSet().apply {
                playSequentially(image, title, email, password, together)
                start()
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}