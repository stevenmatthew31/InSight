package com.mitsuru.insight

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.mitsuru.insight.databinding.ActivitySignInBinding
import com.mitsuru.insight.information.LoginInformation
import com.mitsuru.insight.response.SignInResponse
import com.mitsuru.insight.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        showLoading(false)
        startAnimation()

        binding.apply {
            btnRegister.setOnClickListener { startActivity(Intent(this@SignInActivity, SignUpActivity::class.java)) }
            btnLogin.setOnClickListener {  }
        }
    }

    private fun login(){
        val loginInfo = LoginInformation()
        binding.apply {
            loginInfo.email = edtEmail.text.toString()
            loginInfo.password = edtPasssword.text.toString()
        }

        showLoading(true)

        val service = ApiConfig().getApiService().loginUser(loginInfo)
        service.enqueue(object : Callback<SignInResponse>{
            override fun onResponse(
                call: Call<SignInResponse>,
                response: Response<SignInResponse>,
            ) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null){
                        Toast.makeText(
                            this@SignInActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignInActivity , MainActivity::class.java)
                        showLoading(false)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
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