package com.mitsuru.insight.response

import com.google.gson.annotations.SerializedName

data class SignInResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("errors")
	val errors: ErrorsLogin? = null
)

data class ErrorsLogin(

	@field:SerializedName("password")
	val password: List<String?>? = null,

	@field:SerializedName("email")
	val email: List<String?>? = null
)
