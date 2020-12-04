package com.example.superherosearch.data.network

import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET

interface SuperHeroApi {
  @GET("AniketSK/d4d9e03d5d2fdfb83199dbb2605e8cf6/" +
    "raw/49983e4225cf2f53ab9d29e3a3b6ed35805518c8/SampleResponse.json")
  fun getSuperHeroes(): Single<Result<GetSuperHeroesResponse>>
}