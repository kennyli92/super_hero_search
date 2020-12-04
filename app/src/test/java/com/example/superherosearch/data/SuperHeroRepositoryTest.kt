package com.example.superherosearch.data

import com.example.superherosearch.data.db.SuperHeroDao
import com.example.superherosearch.data.network.GetSuperHeroesResponse
import com.example.superherosearch.data.network.SuperHeroApi
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.io.IOException

class SuperHeroRepositoryTest {
  @Before
  fun before() {
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
  }

  @After
  fun after() {
    RxAndroidPlugins.reset()
    RxJavaPlugins.reset()
  }

  private val superHeroDao: SuperHeroDao = mock()
  private val superHeroApi: SuperHeroApi = mock()
  private val superHeroCharacters = listOf(
    SuperHeroCharacter(name = "name1", image = Image(url = "url1")),
    SuperHeroCharacter(name = "name2", image = Image(url = "url2")),
    SuperHeroCharacter(name = "name3", image = Image(url = "url3"))
  )

  private fun superHeroRepository(): SuperHeroRepository {
    return SuperHeroRepository(
      superHeroDao = superHeroDao,
      superHeroApi = superHeroApi
    )
  }

  @Test
  fun get_super_heroes_from_api_returns_200() {
    val superHeroRepository = superHeroRepository()
    val successResponse = Response.success(GetSuperHeroesResponse(characters = superHeroCharacters))
    whenever(superHeroApi.getSuperHeroes()).doReturn(Single.just(Result.response(successResponse)))

    superHeroRepository.getSuperHeroes(isCache = false).test()
      .assertComplete()
      .assertNoErrors()
      .assertValueCount(1)
      .assertValueAt(0, SuperHeroesResponse.Characters(characters = superHeroCharacters))
  }

  @Test
  fun get_super_heroes_from_api_returns_404() {
    val superHeroRepository = superHeroRepository()
    val responseBody = ResponseBody.create(
      MediaType.parse("application/json"), "content")
    val notFoundResponse = Response.error<GetSuperHeroesResponse>(404, responseBody)
    whenever(superHeroApi.getSuperHeroes()).doReturn(Single.just(Result.response(notFoundResponse)))

    superHeroRepository.getSuperHeroes(isCache = false).test()
      .assertComplete()
      .assertNoErrors()
      .assertValueCount(1)
      .assertValueAt(0, SuperHeroesResponse.NotFound)
  }

  @Test
  fun get_super_heroes_from_api_returns_500() {
    val superHeroRepository = superHeroRepository()
    val responseBody = ResponseBody.create(
      MediaType.parse("application/json"), "content")
    val unhandledResponse = Response.error<GetSuperHeroesResponse>(500, responseBody)
    whenever(superHeroApi.getSuperHeroes())
      .doReturn(Single.just(Result.response(unhandledResponse)))

    val values = superHeroRepository.getSuperHeroes(isCache = false).test()
      .assertComplete()
      .assertNoErrors()
      .assertValueCount(1)
      .values()

    assertThat(values[0]).isInstanceOf(SuperHeroesResponse.UnknownError::class.java)
    assertThat((values[0] as SuperHeroesResponse.UnknownError).throwable)
      .isInstanceOf(IOException::class.java)
    assertThat((values[0] as SuperHeroesResponse.UnknownError).throwable.message)
      .isEqualTo("Unhandled code: 500")
  }

  @Test
  fun get_super_heroes_from_db_returns_data() {
    val superHeroRepository = superHeroRepository()
    whenever(superHeroDao.getAllSuperHeroCharacters()).doReturn(Flowable.just(superHeroCharacters))

    superHeroRepository.getSuperHeroes(isCache = true).test()
      .assertComplete()
      .assertNoErrors()
      .assertValueCount(1)
      .assertValueAt(0, SuperHeroesResponse.Characters(characters = superHeroCharacters))
  }
}