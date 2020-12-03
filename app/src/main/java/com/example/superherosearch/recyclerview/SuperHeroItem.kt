package com.example.superherosearch.recyclerview

sealed class SuperHeroItem {
  data class Image(
      val name: String,
      val imageUrl: String
  ) : SuperHeroItem()
}