package com.delex.ffsensiboost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delex.ffsensiboost.ui.theme.BackgroundDark
import com.delex.ffsensiboost.ui.theme.ButtonNeon
import com.delex.ffsensiboost.ui.theme.CardBackground
import com.delex.ffsensiboost.ui.theme.ErrorRed
import com.delex.ffsensiboost.ui.theme.PrimaryNeon
import com.delex.ffsensiboost.ui.theme.SecondaryNeon
import com.delex.ffsensiboost.ui.theme.SurfaceDark
import com.delex.ffsensiboost.ui.theme.SurfaceVariant
import com.delex.ffsensiboost.ui.theme.SuccessGreen
import com.delex.ffsensiboost.ui.theme.TextPrimary
import com.delex.ffsensiboost.ui.theme.TextSecondary
import com.delex.ffsensiboost.viewmodel.BoosterViewModel
import com.delex.ffsensiboost.viewmodel.SensitivityViewModel

/**
 * Main Dashboard Screen - Entry point of the app
 * Shows options for Sensitivity Generation and System Booster
 */
@Composable
fun MainDashboardScreen(
    sensitivityViewModel: SensitivityViewModel,
    boosterViewModel: BoosterViewModel,
    onNavigateToSensitivity: () -> Unit,
    onNavigateToBooster: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "FF SENSI BOOST",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryNeon
        )

        Text(
            text = "Fire Force Optimization",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Sensitivity Module Card
        FeatureCard(
            title = "Sensitivity Generator",
            description = "Optimize your Free Fire aim with AI-powered sensitivity calculation",
            icon = "🎯",
            color = PrimaryNeon,
            onClick = {
                sensitivityViewModel.resetTest()
                sensitivityViewModel.scanDeviceSpecifications()
                onNavigateToSensitivity()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Booster Module Card
        FeatureCard(
            title = "System Booster",
            description = "Clear cache, optimize RAM, and boost device performance",
            icon = "⚡",
            color = SecondaryNeon,
            onClick = {
                boosterViewModel.resetBooster()
                boosterViewModel.scanSystem()
                onNavigateToBooster()
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Stats Section
        StatsCard()

        Spacer(modifier = Modifier.height(20.dp))

        // Footer
        Text(
            text = "v1.0.0 | Android 26+",
            fontSize = 10.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Feature Card Component
 */
@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 40.sp,
                    modifier = Modifier.width(60.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 2
                    )
                }

                Text(
                    text = "→",
                    fontSize = 24.sp,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(color)
            )
        }
    }
}

/**
 * Stats Card showing app overview
 */
@Composable
fun StatsCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Quick Stats",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Profiles", value = "1")
                StatItem(label = "Optimized", value = "Yes")
                StatItem(label = "Status", value = "Active")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ButtonNeon
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}
