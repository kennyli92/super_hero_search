package com.example.superherosearch.dagger

import com.example.superherosearch.data.SuperHeroRepository
import com.example.superherosearch.data.db.DaoFactory
import com.example.superherosearch.data.db.RoomDb
import com.example.superherosearch.data.db.SuperHeroDao
import com.example.superherosearch.data.network.SuperHeroApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
  @ActivityScoped
  @Provides
  fun providesSuperHeroDao(daoFactory: DaoFactory) = daoFactory.superHeroDao()

  @ActivityScoped
  @Provides
  fun providesSuperHeroApi(
    retrofit: Retrofit
  ): SuperHeroApi {
    return retrofit.create(SuperHeroApi::class.java)
  }

  @ActivityScoped
  @Provides
  fun providesSuperHeroRepository(
    superHeroApi: SuperHeroApi,
    superHeroDao: SuperHeroDao
  ): SuperHeroRepository {
    return SuperHeroRepository(superHeroApi = superHeroApi, superHeroDao = superHeroDao)
  }
}