package com.example.superherosearch.businesslogic

sealed class SuperHeroAction {
  object Load : SuperHeroAction()
  data class GetSuperHeroes(val isCache: Boolean = true) : SuperHeroAction()
  data class Search(val query: String) : SuperHeroAction()
}