package com.example.ui.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.CluckCheckViewModel
import com.example.ui.DisclaimerGateState
import com.example.ui.screens.AddFoodScreen
import com.example.ui.screens.CoopRulesScreen
import com.example.ui.screens.DisclaimerGateScreen
import com.example.ui.screens.FoodDetailScreen
import com.example.ui.screens.FoodGuideScreen
import com.example.ui.screens.SavedScreen

const val ROUTE_FOOD_GUIDE = "food_guide"
const val ROUTE_COOP_RULES = "coop_rules"
const val ROUTE_SAVED = "saved"
const val ROUTE_FOOD_DETAIL = "food_detail/{foodId}"
const val ROUTE_ADD_FOOD = "add_food?query={query}"

private data class TopLevelTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TopLevelTabs = listOf(
    TopLevelTab(
        route = ROUTE_FOOD_GUIDE,
        labelRes = R.string.nav_search,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        testTag = "nav_tab_guide"
    ),
    TopLevelTab(
        route = ROUTE_COOP_RULES,
        labelRes = R.string.nav_rules,
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook,
        testTag = "nav_tab_rules"
    ),
    TopLevelTab(
        route = ROUTE_SAVED,
        labelRes = R.string.nav_saved,
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder,
        testTag = "nav_tab_saved"
    )
)

@Composable
fun CluckNavHost(
    viewModel: CluckCheckViewModel,
    modifier: Modifier = Modifier
) {
    val disclaimerState by viewModel.disclaimerState.collectAsStateWithLifecycle()

    when (disclaimerState) {
        DisclaimerGateState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
            return
        }
        DisclaimerGateState.NeedsAcceptance -> {
            DisclaimerGateScreen(
                onAccept = viewModel::acceptDisclaimer,
                modifier = modifier
            )
            return
        }
        DisclaimerGateState.Accepted -> Unit
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTopLevelRoute = TopLevelTabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    val foodGuideState by viewModel.foodGuideUiState.collectAsStateWithLifecycle()
    val savedFoods by viewModel.savedFoods.collectAsStateWithLifecycle()
    val savedVerdictFilter by viewModel.savedVerdictFilter.collectAsStateWithLifecycle()

    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (isTopLevelRoute) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 10.dp
                ) {
                    Column {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 0.dp
                        ) {
                            TopLevelTabs.forEach { tab ->
                                val selected = currentDestination?.hierarchy?.any {
                                    it.route == tab.route
                                } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(tab.labelRes)) },
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = ROUTE_FOOD_GUIDE
            ) {
                        composable(ROUTE_FOOD_GUIDE) {
                            FoodGuideScreen(
                                uiState = foodGuideState,
                                onQueryChange = viewModel::updateSearchQuery,
                                onVerdictSelect = viewModel::selectVerdict,
                                onCategorySelect = viewModel::selectCategory,
                                onClearFilters = viewModel::clearFilters,
                                onFoodClick = { foodId ->
                                    navController.navigate("food_detail/$foodId")
                                },
                                onToggleFavorite = { food ->
                                    viewModel.toggleFavorite(food.id, food.isFavorite)
                                },
                                onOpenLookupFood = { initialQuery ->
                                    val encoded = Uri.encode(initialQuery.trim())
                                    navController.navigate("add_food?query=$encoded")
                                }
                            )
                        }

                        composable(ROUTE_COOP_RULES) {
                            CoopRulesScreen()
                        }

                        composable(ROUTE_SAVED) {
                            SavedScreen(
                                savedFoods = savedFoods,
                                selectedVerdict = savedVerdictFilter,
                                onSelectVerdict = viewModel::selectSavedVerdict,
                                onFoodClick = { foodId ->
                                    navController.navigate("food_detail/$foodId")
                                },
                                onToggleFavorite = { food ->
                                    viewModel.toggleFavorite(food.id, food.isFavorite)
                                }
                            )
                        }

                        composable(
                            route = ROUTE_FOOD_DETAIL,
                            arguments = listOf(navArgument("foodId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val foodId = backStackEntry.arguments?.getInt("foodId") ?: 1
                            val food by viewModel.observeFoodDetail(foodId)
                                .collectAsStateWithLifecycle(initialValue = null)
                            FoodDetailScreen(
                                food = food,
                                onBack = { navController.popBackStack() },
                                onToggleFavorite = { current ->
                                    viewModel.toggleFavorite(current.id, current.isFavorite)
                                },
                                onSaveNote = viewModel::saveUserNote,
                                onDeleteCustomFood = { id ->
                                    viewModel.deleteCustomFood(id) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }

                        composable(
                            route = ROUTE_ADD_FOOD,
                            arguments = listOf(
                                navArgument("query") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) { backStackEntry ->
                            val initialQuery = backStackEntry.arguments?.getString("query").orEmpty()
                            val lookupQuery by viewModel.lookupQuery.collectAsStateWithLifecycle()
                            val lookupUiState by viewModel.lookupUiState.collectAsStateWithLifecycle()
                            AddFoodScreen(
                                initialQuery = initialQuery,
                                lookupQuery = lookupQuery,
                                lookupUiState = lookupUiState,
                                onPrepareLookup = viewModel::prepareFoodLookup,
                                onQueryChange = viewModel::updateLookupQuery,
                                onLookupClick = { viewModel.lookupFood() },
                                onManualEntryClick = viewModel::startManualDraft,
                                onUpdateDraft = viewModel::updateDraft,
                                onSaveFood = { saveAsFavorite, note ->
                                    viewModel.saveDraftToDatabase(
                                        saveAsFavorite = saveAsFavorite,
                                        userNote = note
                                    ) {
                                        navController.popBackStack()
                                    }
                                },
                                onViewExisting = { existingId ->
                                    navController.navigate("food_detail/$existingId") {
                                        popUpTo(ROUTE_FOOD_GUIDE)
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
            }
        }
    }
}
