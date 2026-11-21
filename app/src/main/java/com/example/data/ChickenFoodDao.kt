package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChickenFoodDao {
    @Query(
        """
        SELECT * FROM chicken_foods
        WHERE (:verdict IS NULL OR verdict = :verdict)
          AND (:category IS NULL OR category = :category)
          AND (
              :query = ''
              OR name LIKE '%' || :query || '%'
              OR aliases LIKE '%' || :query || '%'
              OR shortVerdict LIKE '%' || :query || '%'
              OR whyDetails LIKE '%' || :query || '%'
              OR preparationSteps LIKE '%' || :query || '%'
              OR nutritionalValue LIKE '%' || :query || '%'
              OR macroHighlights LIKE '%' || :query || '%'
          )
        ORDER BY popularityRank ASC, name ASC
        """
    )
    fun observeFoods(
        query: String,
        verdict: SafetyVerdict?,
        category: FoodCategory?
    ): Flow<List<ChickenFoodEntity>>

    @Query(
        """
        SELECT * FROM chicken_foods
        WHERE isFavorite = 1
          AND (:verdict IS NULL OR verdict = :verdict)
        ORDER BY name ASC
        """
    )
    fun observeFavorites(verdict: SafetyVerdict?): Flow<List<ChickenFoodEntity>>

    @Query("SELECT * FROM chicken_foods WHERE id = :id LIMIT 1")
    fun observeFoodById(id: Int): Flow<ChickenFoodEntity?>

    @Query("SELECT COUNT(*) FROM chicken_foods")
    suspend fun getFoodCount(): Int

    @Query("SELECT COALESCE(MAX(id), 0) FROM chicken_foods")
    suspend fun getMaxFoodId(): Int

    @Query("SELECT * FROM chicken_foods WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByNameIgnoreCase(name: String): ChickenFoodEntity?

    @Query("SELECT * FROM chicken_foods ORDER BY id ASC")
    suspend fun getAllFoodsOnce(): List<ChickenFoodEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(foods: List<ChickenFoodEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: ChickenFoodEntity)

    @Query("DELETE FROM chicken_foods WHERE id = :id")
    suspend fun deleteFoodById(id: Int)

    @Query("UPDATE chicken_foods SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("UPDATE chicken_foods SET userNote = :note WHERE id = :id")
    suspend fun updateUserNote(id: Int, note: String)
}
