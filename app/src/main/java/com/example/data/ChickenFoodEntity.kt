package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chicken_foods")
data class ChickenFoodEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val aliases: String,
    val emoji: String,
    val category: FoodCategory,
    val verdict: SafetyVerdict,
    val shortVerdict: String,
    val whyDetails: String,
    val preparationSteps: String,
    val servingGuidance: String,
    val partsToAvoid: String,
    val nutritionalValue: String,
    val macroHighlights: String,
    val excessWarning: String = "",
    val isFavorite: Boolean = false,
    val userNote: String = "",
    val popularityRank: Int = 100
)
