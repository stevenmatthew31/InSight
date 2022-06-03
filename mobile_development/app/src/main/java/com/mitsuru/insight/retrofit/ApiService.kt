package com.mitsuru.insight.retrofit

import com.mitsuru.insight.information.LoginInformation
import com.mitsuru.insight.information.RegisterInformation
import com.mitsuru.insight.response.SignInResponse
import com.mitsuru.insight.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun registerUser(
        @Body registerInformation: RegisterInformation
    ): Call<SignUpResponse>

    @POST("login")
    fun loginUser(
        @Body loginInformation : LoginInformation
    ) : Call<SignInResponse>
}