package com.example.superherosearch.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.superherosearch.data.SuperHeroCharacter
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface SuperHeroDao {
  /**
   * Create new super hero character record.s If record already exist, replace it.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(superHeroCharacters: List<SuperHeroCharacter>): List<Long>

  @Query("Delete FROM SuperHeroCharacters")
  fun deleteAll()

  /**
   * Delete all super hero characters from the table before insert a fresh set of
   * super hero characters
   */
  @Transaction
  fun updateAll(superHeroCharacters: List<SuperHeroCharacter>): List<Long> {
    deleteAll()
    return insertAll(superHeroCharacters = superHeroCharacters)
  }

  @Query("SELECT * FROM SuperHeroCharacters")
  fun getAllSuperHeroCharacters(): Flowable<List<SuperHeroCharacter>>
}