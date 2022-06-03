package com.mitsuru.insight.response

import com.google.gson.annotations.SerializedName

data class SignUpResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("errors")
	val errors: ErrorsRegister? = null
)

data class ErrorsRegister(

	@field:SerializedName("name")
	val name: List<String?>? = null,

	@field:SerializedName("username")
	val username: List<String?>? = null,

	@field:SerializedName("email")
	val email: List<String?>? = null,

	@field:SerializedName("password")
	val password: List<String?>? = null
)
