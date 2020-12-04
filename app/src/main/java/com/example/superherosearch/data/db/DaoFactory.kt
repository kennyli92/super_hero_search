package com.example.superherosearch.data.db

interface DaoFactory {
  fun superHeroDao(): SuperHeroDao
}