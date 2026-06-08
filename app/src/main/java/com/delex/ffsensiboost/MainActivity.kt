package com.delex.ffsensiboost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.delex.ffsensiboost.ui.screens.BoosterScreen
import com.delex.ffsensiboost.ui.screens.MainDashboardScreen
import com.delex.ffsensiboost.ui.screens.SensitivityScreen
import com.delex.ffsensiboost.ui.theme.FFSensiBoostTheme
import com.delex.ffsensiboost.viewmodel.BoosterViewModel
import com.delex.ffsensiboost.viewmodel.SensitivityViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity - Entry point of the application
 * Handles navigation between Dashboard, Sensitivity, and Booster screens
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FFSensiBoostTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "dashboard"
                ) {
                    // Dashboard Screen
                    composable("dashboard") {
                        val sensitivityViewModel: SensitivityViewModel = hiltViewModel()
                        val boosterViewModel: BoosterViewModel = hiltViewModel()
                        
                        MainDashboardScreen(
                            sensitivityViewModel = sensitivityViewModel,
                            boosterViewModel = boosterViewModel,
                            onNavigateToSensitivity = {
                                navController.navigate("sensitivity")
                            },
                            onNavigateToBooster = {
                                navController.navigate("booster")
                            }
                        )
                    }

                    // Sensitivity Generator Screen
                    composable("sensitivity") {
                        val sensitivityViewModel: SensitivityViewModel = hiltViewModel()
                        
                        SensitivityScreen(
                            sensitivityViewModel = sensitivityViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToResults = {
                                // Navigate to results (can be part of the same screen flow)
                            }
                        )
                    }

                    // System Booster Screen
                    composable("booster") {
                        val boosterViewModel: BoosterViewModel = hiltViewModel()
                        
                        BoosterScreen(
                            boosterViewModel = boosterViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
