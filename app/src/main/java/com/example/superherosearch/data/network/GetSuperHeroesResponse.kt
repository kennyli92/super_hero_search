package com.example.superherosearch.data.network

import com.example.superherosearch.data.SuperHeroCharacter
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSuperHeroesResponse(
  val characters: List<SuperHeroCharacter>
)