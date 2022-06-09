package com.mitsuru.insight

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mitsuru.insight.databinding.ActivitySignUpBinding
import com.mitsuru.insight.information.RegisterInformation
import com.mitsuru.insight.response.SignUpResponse
import com.mitsuru.insight.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        supportActionBar?.hide()
        showLoading(false)
        startAnimation()

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length >= 8) {
                    enableButton()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //do nothing
            }

        })

        binding.btnRegisterNow.setOnClickListener {
            binding.apply {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val passwordresub = edtPasswordresub.text.toString()

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
                    edtPassword.error = "password tidak boleh kosong"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }

                if (password.length < 6){
                    edtPassword.error = "password tidak boleh kurang dari 6 karakter"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }

                if (password != passwordresub){
                    edtPasswordresub.error = "password tidak sesuai"
                    edtPasswordresub.requestFocus()
                    return@setOnClickListener
                } else {
                    enableButton()
                }

                registerFirebase(email , password)
            }
        }
    }

    private fun registerFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(
                        this,
                        "Registrasi berhasil",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, SignInActivity::class.java)
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

    /*private fun register(){
        val registerInfo = RegisterInformation()
        binding.apply {
            registerInfo.name = edtName.text.toString()
            registerInfo.username = edtUsername.text.toString()
            registerInfo.email = edtEmail.text.toString()
            registerInfo.password = edtPassword.text.toString()
            registerInfo.resubPassword = edtPasswordresub.text.toString()
        }

        showLoading(true)

        val service = ApiConfig().getApiService().registerUser(registerInfo)
        service.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>,
            ) {
                if (response.isSuccessful){
                    val responseBody  =  response.body()
                    if (responseBody != null){
                        Toast.makeText(
                            this@SignUpActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignUpActivity , SignInActivity::class.java)
                        showLoading(false)
                        startActivity(intent)
                        finish()
                    } else{
                        Toast.makeText(
                            this@SignUpActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        showLoading(false)
                    }
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, t.message, Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        })
    }*/

    private fun enableButton() {
        val result = binding.edtPassword.text
        binding.btnRegisterNow.isEnabled = result != null && result.toString().isNotEmpty()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
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
            val title = ObjectAnimator.ofFloat(titleRegister, View.ALPHA, 1f).setDuration(500)
            val email = ObjectAnimator.ofFloat(edtEmail, View.ALPHA, 1f).setDuration(500)
            val name = ObjectAnimator.ofFloat(edtName, View.ALPHA, 1f).setDuration(500)
            val username = ObjectAnimator.ofFloat(edtUsername,View.ALPHA, 1f).setDuration(500)
            val password = ObjectAnimator.ofFloat(edtPassword, View.ALPHA, 1f).setDuration(500)
            val passwordresub = ObjectAnimator.ofFloat(edtPasswordresub, View.ALPHA, 1f).setDuration(500)
            val btnRegister =
                ObjectAnimator.ofFloat(btnRegisterNow, View.ALPHA, 1f).setDuration(500)



            AnimatorSet().apply {
                playSequentially(
                    image,
                    title,
                    name,
                    username,
                    email,
                    password,
                    passwordresub,
                    btnRegister
                )
                start()
            }
        }
    }
}