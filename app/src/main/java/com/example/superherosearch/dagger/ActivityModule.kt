package com.example.superherosearch.dagger

import com.example.superherosearch.data.SuperHeroApi
import com.example.superherosearch.data.SuperHeroRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
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
    superHeroApi: SuperHeroApi
  ): SuperHeroRepository {
    return SuperHeroRepository(superHeroApi = superHeroApi)
  }
}