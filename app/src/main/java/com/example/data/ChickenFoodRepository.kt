package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class FoodDraft(
    val name: String = "",
    val aliases: String = "",
    val emoji: String = "🌾",
    val category: FoodCategory = FoodCategory.SCRAPS_PANTRY,
    val verdict: SafetyVerdict = SafetyVerdict.PREP_REQUIRED,
    val shortVerdict: String = "",
    val whyDetails: String = "",
    val preparationSteps: String = "",
    val servingGuidance: String = "",
    val partsToAvoid: String = "",
    val nutritionalValue: String = "",
    val macroHighlights: String = "",
    val excessWarning: String = ""
)

class ChickenFoodRepository(private val dao: ChickenFoodDao) {
    private val seedMutex = Mutex()

    suspend fun ensureSeeded() {
        seedMutex.withLock {
            if (dao.getFoodCount() == 0) {
                dao.insertAll(ChickenFoodSeedPart1.items + ChickenFoodSeedPart2.items)
            }
        }
    }

    fun observeFoods(
        query: String,
        verdict: SafetyVerdict?,
        category: FoodCategory?
    ): Flow<List<ChickenFoodEntity>> = dao.observeFoods(
        query = query.trim(),
        verdict = verdict,
        category = category
    )

    fun observeFavorites(verdict: SafetyVerdict?): Flow<List<ChickenFoodEntity>> =
        dao.observeFavorites(verdict = verdict)

    fun observeFoodById(id: Int): Flow<ChickenFoodEntity?> = dao.observeFoodById(id)

    suspend fun findExistingByName(name: String): ChickenFoodEntity? =
        dao.findByNameIgnoreCase(name.trim())

    suspend fun saveCustomFood(
        draft: FoodDraft,
        existingFood: ChickenFoodEntity? = null,
        saveAsFavorite: Boolean = false,
        userNote: String = ""
    ): ChickenFoodEntity {
        val cleanedName = draft.name.trim()
        val matched = existingFood ?: dao.findByNameIgnoreCase(cleanedName)
        val assignedId = matched?.id ?: maxOf(dao.getMaxFoodId() + 1, 61)
        val assignedRank = matched?.popularityRank ?: 0

        val entity = ChickenFoodEntity(
            id = assignedId,
            name = cleanedName,
            aliases = draft.aliases.trim(),
            emoji = draft.emoji.trim().ifBlank { "🌾" },
            category = draft.category,
            verdict = draft.verdict,
            shortVerdict = draft.shortVerdict.trim(),
            whyDetails = draft.whyDetails.trim().ifBlank { draft.shortVerdict.trim() },
            preparationSteps = draft.preparationSteps.trim().ifBlank {
                "Inspect for freshness and mold before offering small portions."
            },
            servingGuidance = draft.servingGuidance.trim().ifBlank {
                "Keep treats within 10% of daily balanced feed intake."
            },
            partsToAvoid = draft.partsToAvoid.trim().ifBlank {
                "Spoiled, salted, seasoned, or moldy portions"
            },
            nutritionalValue = draft.nutritionalValue.trim().ifBlank {
                "Added to your custom flock food guide."
            },
            macroHighlights = draft.macroHighlights.trim(),
            excessWarning = draft.excessWarning.trim(),
            isFavorite = matched?.isFavorite ?: saveAsFavorite,
            userNote = userNote.trim().ifBlank { matched?.userNote.orEmpty() },
            popularityRank = assignedRank
        )

        dao.insertFood(entity)
        return entity
    }

    suspend fun deleteCustomFood(id: Int) {
        if (id > 60) {
            dao.deleteFoodById(id)
        }
    }

    suspend fun toggleFavorite(id: Int, currentlyFavorite: Boolean) {
        dao.updateFavorite(id = id, isFavorite = !currentlyFavorite)
    }

    suspend fun saveUserNote(id: Int, note: String) {
        dao.updateUserNote(id = id, note = note.trim())
    }
}
