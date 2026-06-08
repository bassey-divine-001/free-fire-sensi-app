package com.delex.ffsensiboost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delex.ffsensiboost.ui.components.AdvancedSwipeTestArea
import com.delex.ffsensiboost.ui.theme.BackgroundDark
import com.delex.ffsensiboost.ui.theme.ButtonHover
import com.delex.ffsensiboost.ui.theme.ButtonNeon
import com.delex.ffsensiboost.ui.theme.CardBackground
import com.delex.ffsensiboost.ui.theme.ErrorRed
import com.delex.ffsensiboost.ui.theme.PrimaryNeon
import com.delex.ffsensiboost.ui.theme.SurfaceDark
import com.delex.ffsensiboost.ui.theme.SurfaceVariant
import com.delex.ffsensiboost.ui.theme.TextPrimary
import com.delex.ffsensiboost.ui.theme.TextSecondary
import com.delex.ffsensiboost.viewmodel.SensitivityViewModel

/**
 * Sensitivity Screen - Main sensitivity generation interface
 * Displays device info and initiates swipe test
 */
@Composable
fun SensitivityScreen(
    sensitivityViewModel: SensitivityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResults: () -> Unit
) {
    val deviceSpecs by sensitivityViewModel.deviceSpecs.collectAsState()
    val isLoading by sensitivityViewModel.isLoading.collectAsState()
    val testProgress by sensitivityViewModel.testProgress.collectAsState()
    val generatedConfig by sensitivityViewModel.generatedConfig.collectAsState()
    
    var currentScreen by remember { mutableStateOf("info") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryNeon)
            }

            Text(
                text = "SENSITIVITY GENERATOR",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryNeon
            )

            Box(modifier = Modifier.padding(8.dp))
        }

        // Main content
        when (currentScreen) {
            "info" -> DeviceInfoScreen(
                deviceSpecs = deviceSpecs,
                onStartTest = { currentScreen = "test" }
            )
            "test" -> SwipeTestScreen(
                sensitivityViewModel = sensitivityViewModel,
                onTestComplete = {
                    currentScreen = "results"
                    sensitivityViewModel.generateSensitivityConfiguration()
                }
            )
            "results" -> ResultsScreen(
                sensitivityViewModel = sensitivityViewModel,
                onBackToHome = onNavigateBack
            )
        }
    }
}

/**
 * Device Information Screen
 */
@Composable
fun DeviceInfoScreen(
    deviceSpecs: com.delex.ffsensiboost.data.model.DeviceSpecifications?,
    onStartTest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📱 Device Analysis",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (deviceSpecs != null) {
            // Device Performance Rating
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Performance Rating",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = String.format("%.1f", deviceSpecs.getPerformanceRating()),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryNeon
                    )

                    Text(
                        text = "/100",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Device Details
            DeviceDetailCard(
                title = "Device",
                value = "${deviceSpecs.deviceManufacturer} ${deviceSpecs.deviceModel}"
            )

            DeviceDetailCard(
                title = "Android Version",
                value = "${deviceSpecs.androidVersion} (API ${deviceSpecs.apiLevel})"
            )

            DeviceDetailCard(
                title = "RAM",
                value = "${deviceSpecs.totalRAM / 1_000_000_000}GB (${deviceSpecs.availableRAM / 1_000_000_000}GB Available)"
            )

            DeviceDetailCard(
                title = "Processor",
                value = "${deviceSpecs.processorCores} Cores"
            )

            DeviceDetailCard(
                title = "Display",
                value = "${deviceSpecs.screenResolutionWidth}x${deviceSpecs.screenResolutionHeight} @ ${deviceSpecs.screenRefreshRate}Hz"
            )

            DeviceDetailCard(
                title = "DPI Setting",
                value = "${deviceSpecs.screenDensityDPI}"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Start Test Button
            Button(
                onClick = onStartTest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonNeon),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "START SWIPE TEST",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }

        } else {
            CircularProgressIndicator(color = ButtonNeon)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Scanning device...", color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DeviceDetailCard(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 14.sp,
            color = PrimaryNeon,
            fontWeight = FontWeight.SemiBold
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Swipe Test Screen
 */
@Composable
fun SwipeTestScreen(
    sensitivityViewModel: SensitivityViewModel,
    onTestComplete: () -> Unit
) {
    val swipeResults by sensitivityViewModel.swipeResults.collectAsState()
    val testProgress by sensitivityViewModel.testProgress.collectAsState()
    
    var currentRound by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(SurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(testProgress / 100f)
                    .height(4.dp)
                    .background(ButtonNeon)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Swipe test area
        AdvancedSwipeTestArea(
            roundNumber = currentRound + 1,
            onSwipeDetected = { speed, _ ->
                sensitivityViewModel.processSwipeTestRound(speed, currentRound)

                if (currentRound < 4) {
                    currentRound++
                } else {
                    onTestComplete()
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Test results so far
        if (swipeResults.isNotEmpty()) {
            Text(
                text = "Results Summary",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            swipeResults.forEachIndexed { index, speed ->
                if (speed > 0f) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Round ${index + 1}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = String.format("%.3f px/ms", speed),
                            fontSize = 12.sp,
                            color = PrimaryNeon,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Results Screen - Display generated sensitivity configuration
 */
@Composable
fun ResultsScreen(
    sensitivityViewModel: SensitivityViewModel,
    onBackToHome: () -> Unit
) {
    val generatedConfig by sensitivityViewModel.generatedConfig.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (generatedConfig != null) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "✓ OPTIMIZATION COMPLETE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryNeon,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Sensitivity Settings
            SettingsBox(
                title = "🎯 AIMING SETTINGS",
                items = listOf(
                    "General" to generatedConfig!!.generalSensitivity.toString(),
                    "Red Dot" to generatedConfig!!.redDotSensitivity.toString(),
                    "2x Scope" to generatedConfig!!.scope2xSensitivity.toString(),
                    "4x Scope" to generatedConfig!!.scope4xSensitivity.toString(),
                    "Sniper" to generatedConfig!!.sniperScopeSensitivity.toString(),
                    "Free Look" to generatedConfig!!.freeLookSensitivity.toString()
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsBox(
                title = "🎮 GAMEPLAY SETTINGS",
                items = listOf(
                    "Fire Button Size" to "${generatedConfig!!.fireButtonSize}%",
                    "Android DPI" to generatedConfig!!.recommendedDPI.toString()
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsBox(
                title = "📊 OPTIMIZATION METRICS",
                items = listOf(
                    "Device Rating" to String.format("%.1f/100", generatedConfig!!.deviceRating),
                    "Swipe Factor" to String.format("%.1f/100", generatedConfig!!.swipeFactor)
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonNeon),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "BACK TO HOME",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

        } else {
            CircularProgressIndicator(color = ButtonNeon)
        }
    }
}

@Composable
fun SettingsBox(
    title: String,
    items: List<Pair<String, String>>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = value,
                        fontSize = 12.sp,
                        color = PrimaryNeon,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
