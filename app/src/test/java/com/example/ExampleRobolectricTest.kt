package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ChickenFoodDatabase
import com.example.data.ChickenFoodRepository
import com.example.data.DataStoreDisclaimerStore
import com.example.data.FoodCategory
import com.example.data.FoodDraft
import com.example.data.SafetyVerdict
import com.example.ui.screens.DisclaimerGateScreen
import com.example.ui.theme.CluckCheckTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: ChickenFoodDatabase
    private lateinit var repository: ChickenFoodRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ChickenFoodDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ChickenFoodRepository(database.chickenFoodDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `app name matches ChickCheck`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ChickCheck", appName)
    }

    @Test
    fun `seeded catalog has 60 items with nutritional values and all verdicts`() = runTest {
        repository.ensureSeeded()
        val allFoods = repository.observeFoods(query = "", verdict = null, category = null).first()

        assertEquals(60, allFoods.size)
        assertTrue(allFoods.all { it.nutritionalValue.isNotBlank() })
        assertTrue(allFoods.all { it.preparationSteps.isNotBlank() })
        assertTrue(allFoods.all { it.whyDetails.isNotBlank() })

        val verdictsPresent = allFoods.map { it.verdict }.toSet()
        assertEquals(SafetyVerdict.entries.toSet(), verdictsPresent)
    }

    @Test
    fun `searching onion explains egg flavor taint and avoid verdict`() = runTest {
        repository.ensureSeeded()
        val results = repository.observeFoods(query = "onion", verdict = null, category = null).first()

        assertTrue(results.any { it.name == "Onions" && it.verdict == SafetyVerdict.AVOID })
        val onion = results.first { it.name == "Onions" }
        assertTrue(onion.whyDetails.contains("egg", ignoreCase = true))
        assertTrue(onion.nutritionalValue.isNotBlank())
    }

    @Test
    fun `toggling favorite and saving flock note persists in Room`() = runTest {
        repository.ensureSeeded()
        val watermelon = repository.observeFoods(query = "watermelon", verdict = null, category = null)
            .first()
            .first()

        repository.toggleFavorite(watermelon.id, watermelon.isFavorite)
        repository.saveUserNote(watermelon.id, "Serve chilled on 95F days")

        val favorites = repository.observeFavorites(null).first()
        assertEquals(1, favorites.size)
        assertEquals("Serve chilled on 95F days", favorites.first().userNote)
    }

    @Test
    fun `disclaimer acceptance defaults false and persists in DataStore`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = DataStoreDisclaimerStore(context)

        assertFalse(store.isDisclaimerAccepted.first())
        store.acceptDisclaimer()
        assertTrue(store.isDisclaimerAccepted.first())
    }

    @Test
    fun `disclaimer gate requires checkbox before enabling accept button`() {
        var accepted = false
        composeTestRule.setContent {
            CluckCheckTheme {
                DisclaimerGateScreen(onAccept = { accepted = true })
            }
        }

        composeTestRule.onNodeWithTag("disclaimer_accept_button")
            .performScrollTo()
            .assertIsNotEnabled()
        composeTestRule.onNodeWithTag("disclaimer_checkbox_row")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("disclaimer_accept_button")
            .performScrollTo()
            .assertIsEnabled()
            .performClick()

        assertTrue(accepted)
    }

    @Test
    fun `saving custom food persists in Room and can be queried and deleted`() = runTest {
        repository.ensureSeeded()

        val saved = repository.saveCustomFood(
            draft = FoodDraft(
                name = "Mango",
                emoji = "🥭",
                category = FoodCategory.FRUITS,
                verdict = SafetyVerdict.PREP_REQUIRED,
                shortVerdict = "Safe flesh in small cubes — remove pit and tough peel",
                whyDetails = "Mango flesh provides Vitamin A and C; the large pit is a choking hazard.",
                preparationSteps = "Peel skin and discard pit; chop ripe flesh into small pieces."
            ),
            saveAsFavorite = true,
            userNote = "Flock loves chilled mango in July"
        )

        assertTrue(saved.id > 60)
        val searchResults = repository.observeFoods(query = "mango", verdict = null, category = null).first()
        assertEquals(1, searchResults.size)
        assertEquals("Mango", searchResults.first().name)
        assertTrue(searchResults.first().isFavorite)

        val allFoods = repository.observeFoods(query = "", verdict = null, category = null).first()
        assertEquals(61, allFoods.size)
        assertEquals("Mango", allFoods.first().name)

        repository.deleteCustomFood(saved.id)
        val afterDelete = repository.observeFoods(query = "", verdict = null, category = null).first()
        assertEquals(60, afterDelete.size)
    }
}
