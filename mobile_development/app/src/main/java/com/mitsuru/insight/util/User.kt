package com.mitsuru.insight.util

data class User(
    val id : String?,
    val email : String? = "",
    val lat : Float = 0f,
    val lon : Float = 0f
)
