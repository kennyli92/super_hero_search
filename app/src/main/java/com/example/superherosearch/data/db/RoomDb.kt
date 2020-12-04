package com.example.superherosearch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.superherosearch.data.SuperHeroCharacter

@Database(entities = [SuperHeroCharacter::class], version = 1)
abstract class RoomDb : RoomDatabase(), DaoFactory