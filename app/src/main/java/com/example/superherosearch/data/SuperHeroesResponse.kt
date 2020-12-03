package com.example.superherosearch.data

sealed class SuperHeroesResponse {
  data class Characters(
    val characters: List<SuperHeroCharacter>
  ) : SuperHeroesResponse()

  object NotFound : SuperHeroesResponse()
  data class UnknownError(
    val throwable: Throwable
  ) : SuperHeroesResponse()
}