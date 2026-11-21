package com.example.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromSafetyVerdict(value: SafetyVerdict): String = value.name

    @TypeConverter
    fun toSafetyVerdict(value: String): SafetyVerdict = SafetyVerdict.valueOf(value)

    @TypeConverter
    fun fromFoodCategory(value: FoodCategory): String = value.name

    @TypeConverter
    fun toFoodCategory(value: String): FoodCategory = FoodCategory.valueOf(value)
}
