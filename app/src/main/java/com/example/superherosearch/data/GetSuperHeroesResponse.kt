package com.example.superherosearch.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSuperHeroesResponse(
  val characters: List<SuperHeroCharacter>
)