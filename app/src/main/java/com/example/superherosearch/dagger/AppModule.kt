package com.example.superherosearch.dagger

import android.content.Context
import androidx.room.Room
import com.example.superherosearch.data.db.RoomDb
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
  @Singleton
  @Provides
  fun providesMoshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  @Singleton
  @Provides
  fun providesRetrofit(
    moshi: Moshi
  ): Retrofit {
    return Retrofit.Builder()
      .baseUrl("https://gist.githubusercontent.com/")
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }

  @Singleton
  @Provides
  fun providesRoomDb(
    @ApplicationContext context: Context
  ): RoomDb {
    return Room.databaseBuilder(context, RoomDb::class.java, "SuperHeroDb").build()
  }
}