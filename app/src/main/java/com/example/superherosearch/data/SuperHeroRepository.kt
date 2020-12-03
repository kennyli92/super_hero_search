package com.example.superherosearch.data

import io.reactivex.Single
import java.io.IOException

class SuperHeroRepository(private val superHeroApi: SuperHeroApi) {
  fun getSuperHeroes(): Single<SuperHeroesResponse> {
    return superHeroApi.getSuperHeroes()
      .map {
        val responseCode = it.response()?.code()
        when {
          responseCode == 200 && it.response()?.body() != null ->
            SuperHeroesResponse.Characters(characters = it.response()!!.body()!!.characters)
          responseCode == 404 -> SuperHeroesResponse.NotFound
          !it.isError && responseCode != null ->
            SuperHeroesResponse.UnknownError(throwable = IOException("Unhandled code: $responseCode"))
          else -> SuperHeroesResponse.UnknownError(throwable = it.error()!!)
        }
      }.onErrorReturn { throwable ->
        SuperHeroesResponse.UnknownError(throwable = throwable)
      }
  }
}