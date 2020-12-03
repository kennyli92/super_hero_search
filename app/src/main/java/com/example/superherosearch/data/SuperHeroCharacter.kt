package com.example.superherosearch.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SuperHeroCharacter(
  val name: String,
  val image: Image
)

@JsonClass(generateAdapter = true)
data class Image(
  val url: String
)