package com.example.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "FoodLookupClient"

sealed interface FoodLookupResult {
    data class Success(val draft: FoodDraft) : FoodLookupResult
    data class Error(val message: String) : FoodLookupResult
}

interface FoodLookupClient {
    suspend fun lookupFood(query: String): FoodLookupResult
}

class FirebaseFoodLookupClient : FoodLookupClient {

    private val foodSchema = Schema.obj(
        properties = mapOf(
            "name" to Schema.string(description = "Clean capitalized common name of the food or plant"),
            "aliases" to Schema.string(description = "Comma-separated alternative names or related forms"),
            "emoji" to Schema.string(description = "Single representative food or plant emoji"),
            "category" to Schema.enumeration(
                values = FoodCategory.entries.map { it.name },
                description = "Best matching food category"
            ),
            "verdict" to Schema.enumeration(
                values = SafetyVerdict.entries.map { it.name },
                description = "SAFE if wholesome as a treat, PREP_REQUIRED if safe only when washed/cooked/peeled/portioned, AVOID if causes egg taint/digestive upset/poor nutrition, DANGEROUS if toxic or fatal"
            ),
            "shortVerdict" to Schema.string(description = "Concise one-line verdict summary under 85 characters"),
            "whyDetails" to Schema.string(description = "Clear poultry nutrition or toxicology explanation including egg-flavor or crop risks"),
            "preparationSteps" to Schema.string(description = "Actionable preparation instructions, or explicit instruction not to feed"),
            "servingGuidance" to Schema.string(description = "Practical serving size and frequency for backyard chickens"),
            "partsToAvoid" to Schema.string(description = "Specific parts or forms to avoid, or 'None when clean and fresh'"),
            "nutritionalValue" to Schema.string(description = "Key vitamins, minerals, protein, fiber, or anti-nutritional notes"),
            "macroHighlights" to Schema.string(description = "2 to 3 short comma-separated nutrient highlights"),
            "excessWarning" to Schema.string(description = "Risk if overfed, or empty string if none")
        ),
        optionalProperties = listOf("excessWarning", "aliases")
    )

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = foodSchema
            }
        )
    }

    override suspend fun lookupFood(query: String): FoodLookupResult {
        val cleaned = query.trim()
        if (cleaned.isBlank()) {
            return FoodLookupResult.Error("Please enter a food, scrap, or plant name to look up.")
        }

        val prompt = """
            You are an evidence-based backyard chicken nutrition and poultry toxicology reference for ChickCheck.
            Evaluate whether adult backyard chickens (hens) can safely eat: "$cleaned".
            Rules:
            - Prioritize avian safety. If a food contains known avian toxins (e.g., persin, solanine, theobromine, cyanogenic glycosides, high salt, mold/mycotoxins) or causes lethal crop impaction, classify as DANGEROUS or PREP_REQUIRED as appropriate.
            - If it taints egg flavor or causes loose droppings without acute toxicity, classify as AVOID.
            - Keep shortVerdict crisp and practical (under 85 characters).
            - Return strictly valid JSON matching the schema.
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }
            val jsonText = response.text
                ?: return FoodLookupResult.Error("ChickCheck lookup returned an empty response. Please try again.")

            val json = JSONObject(jsonText)
            val categoryName = json.optString("category", FoodCategory.SCRAPS_PANTRY.name)
            val verdictName = json.optString("verdict", SafetyVerdict.PREP_REQUIRED.name)

            val draft = FoodDraft(
                name = json.optString("name", cleaned).ifBlank { cleaned },
                aliases = json.optString("aliases", ""),
                emoji = json.optString("emoji", "🌾").ifBlank { "🌾" },
                category = FoodCategory.entries.firstOrNull { it.name == categoryName }
                    ?: FoodCategory.SCRAPS_PANTRY,
                verdict = SafetyVerdict.entries.firstOrNull { it.name == verdictName }
                    ?: SafetyVerdict.PREP_REQUIRED,
                shortVerdict = json.optString("shortVerdict", ""),
                whyDetails = json.optString("whyDetails", ""),
                preparationSteps = json.optString("preparationSteps", ""),
                servingGuidance = json.optString("servingGuidance", ""),
                partsToAvoid = json.optString("partsToAvoid", ""),
                nutritionalValue = json.optString("nutritionalValue", ""),
                macroHighlights = json.optString("macroHighlights", ""),
                excessWarning = json.optString("excessWarning", "")
            )
            FoodLookupResult.Success(draft)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Food lookup timed out for '$cleaned'", e)
            FoodLookupResult.Error("Lookup timed out after 60 seconds. Check your connection and try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Food lookup failed for '$cleaned'", e)
            FoodLookupResult.Error(
                e.localizedMessage ?: "Unable to look up '$cleaned' right now. You can still enter it manually."
            )
        }
    }
}
