package com.example.superherosearch.businesslogic

import com.example.superherosearch.dialog.SnackbarViewModel

sealed class SuperHeroEvent {
  object Noop : SuperHeroEvent()
  data class Snackbar(
    val vm: SnackbarViewModel
  ) : SuperHeroEvent()
}