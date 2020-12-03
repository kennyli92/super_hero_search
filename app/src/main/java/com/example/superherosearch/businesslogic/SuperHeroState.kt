package com.example.superherosearch.businesslogic

import com.example.superherosearch.recyclerview.SuperHeroItem
import com.example.superherosearch.utils.Visibility

sealed class SuperHeroState {
  object Noop : SuperHeroState()
  data class ListItem(
    val superHeroItems: List<SuperHeroItem>
  ) : SuperHeroState()
  data class ProcessVisibility(
    val visibility: Visibility
  ) : SuperHeroState()
}