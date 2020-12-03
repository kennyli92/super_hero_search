package com.example.superherosearch.businesslogic

sealed class SuperHeroAction {
  data class LoadSuperHeroes(val isCache: Boolean = true) : SuperHeroAction()
}