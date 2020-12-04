package com.example.superherosearch.data

import androidx.annotation.VisibleForTesting
import com.example.superherosearch.data.db.SuperHeroDao
import com.example.superherosearch.data.network.SuperHeroApi
import io.reactivex.Completable
import io.reactivex.Single
import java.io.IOException

class SuperHeroRepository(
  private val superHeroApi: SuperHeroApi,
  private val superHeroDao: SuperHeroDao
) {

  @VisibleForTesting
  internal fun getSuperHeroesFromApi(): Single<SuperHeroesResponse> {
    return superHeroApi.getSuperHeroes()
      .flatMap {
        val responseCode = it.response()?.code()
        return@flatMap when {
          responseCode == 200 && it.response()?.body() != null -> {
            val characters = it.response()!!.body()!!.characters
            // refresh db with latest super hero characters from api response
            Completable.fromCallable {
              superHeroDao.updateAll(superHeroCharacters = characters)
            }.toSingle {
              SuperHeroesResponse.Characters(characters = characters)
            }.onErrorReturn {
              SuperHeroesResponse.Characters(characters = characters)
            }
          }
          responseCode == 404 -> {
            Single.just(SuperHeroesResponse.NotFound)
          }
          !it.isError && responseCode != null -> {
            Single.just(
              SuperHeroesResponse.UnknownError(
                throwable = IOException("Unhandled code: $responseCode")
              )
            )
          }
          else -> Single.just(
            SuperHeroesResponse.UnknownError(throwable = it.error()!!)
          )
        }
      }.onErrorReturn { throwable ->
        SuperHeroesResponse.UnknownError(throwable = throwable)
      }
  }

  @VisibleForTesting
  internal fun getSuperHeroesFromDb(): Single<SuperHeroesResponse> {
    return superHeroDao.getAllSuperHeroCharacters().firstOrError()
      .flatMap { superHeroCharacters ->
        if (superHeroCharacters.isNotEmpty()) {
          Single.just(
            SuperHeroesResponse.Characters(characters = superHeroCharacters) as SuperHeroesResponse)
        } else {
          getSuperHeroesFromApi()
        }
      }.onErrorResumeNext {
        // fallback to network call to fetch super hero characters if db failed to get
        getSuperHeroesFromApi()
      }
  }

  fun getSuperHeroes(isCache: Boolean): Single<SuperHeroesResponse> {
    return if (isCache) {
      getSuperHeroesFromDb()
    } else {
      getSuperHeroesFromApi()
    }
  }

  /**
   * Search for super hero that begins with query
   */
  fun searchSuperHeroes(query: String): Single<SuperHeroesResponse> {
    return superHeroDao.searchSuperHeroCharacters(query = query).firstOrError()
      .map { characters ->
        SuperHeroesResponse.Characters(characters = characters) as SuperHeroesResponse
      }.onErrorReturn {
        SuperHeroesResponse.NotFound
      }
  }
}