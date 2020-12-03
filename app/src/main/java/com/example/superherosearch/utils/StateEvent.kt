package com.example.superherosearch.utils

data class StateEvent<out S, out E>(
  val state: S,
  val event: E
)