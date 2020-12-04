package com.example.superherosearch.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "SuperHeroCharacters")
data class SuperHeroCharacter(
  @PrimaryKey val name: String,
  @Embedded val image: Image
)

@JsonClass(generateAdapter = true)
data class Image(
  val url: String
)