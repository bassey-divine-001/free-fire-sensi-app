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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.delex.ffsensiboost.ui.theme.ErrorRed
import com.delex.ffsensiboost.ui.theme.PrimaryNeon
import com.delex.ffsensiboost.ui.theme.SecondaryNeon
import com.delex.ffsensiboost.ui.theme.SurfaceDark
import com.delex.ffsensiboost.ui.theme.SurfaceVariant
import com.delex.ffsensiboost.ui.theme.SuccessGreen
import com.delex.ffsensiboost.ui.theme.TextPrimary
import com.delex.ffsensiboost.ui.theme.TextSecondary
import com.delex.ffsensiboost.ui.theme.WarningOrange
import com.delex.ffsensiboost.viewmodel.BoosterViewModel

/**
 * System Booster Screen
 * Handles cache clearing, temporary file removal, and RAM optimization
 */
@Composable
fun BoosterScreen(
    boosterViewModel: BoosterViewModel,
    onNavigateBack: () -> Unit
) {
    val boosterStats by boosterViewModel.boosterStats.collectAsState()
    val isOptimizing by boosterViewModel.isOptimizing.collectAsState()
    val optimizationProgress by boosterViewModel.optimizationProgress.collectAsState()
    val boosterMessage by boosterViewModel.boosterMessage.collectAsState()
    val backgroundProcesses by boosterViewModel.backgroundProcesses.collectAsState()

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
                text = "SYSTEM BOOSTER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryNeon
            )

            Box(modifier = Modifier.padding(8.dp))
        }

        // Content
        if (isOptimizing) {
            BoostingInProgressScreen(
                progress = optimizationProgress,
                message = boosterMessage
            )
        } else if (boosterStats != null) {
            BoostResultsScreen(
                stats = boosterStats!!,
                backgroundProcesses = backgroundProcesses,
                onBoostAgain = { boosterViewModel.executeBoost() },
                onBack = onNavigateBack
            )
        } else {
            BoostScanScreen(
                onStartBoost = { boosterViewModel.executeBoost() },
                onScan = { boosterViewModel.scanSystem() }
            )
        }
    }
}

/**
 * Initial scan screen
 */
@Composable
fun BoostScanScreen(
    onStartBoost: () -> Unit,
    onScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "⚡",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "System Booster",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Optimize your device performance by clearing cache and temporary files",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Features
        BoostFeature(icon = "💾", title = "Clear Cache", desc = "Remove cached files")
        BoostFeature(icon = "🗑️", title = "Clean Trash", desc = "Delete temporary files")
        BoostFeature(icon = "🧠", title = "Optimize RAM", desc = "Free up memory")
        BoostFeature(icon = "⚙️", title = "Kill Processes", desc = "Stop background apps")

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = onStartBoost,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryNeon),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "START BOOST",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Black
            )
        }
    }
}

@Composable
fun BoostFeature(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 24.sp)

        Spacer(modifier = Modifier.padding(start = 12.dp))

        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = desc, fontSize = 11.sp, color = TextSecondary)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Boosting in progress screen
 */
@Composable
fun BoostingInProgressScreen(
    progress: Int,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = SecondaryNeon,
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Optimizing...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = SecondaryNeon
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$progress%",
            fontSize = 18.sp,
            color = SecondaryNeon,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = message,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Results screen after boost
 */
@Composable
fun BoostResultsScreen(
    stats: com.delex.ffsensiboost.data.model.BoosterStats,
    backgroundProcesses: Int,
    onBoostAgain: () -> Unit,
    onBack: () -> Unit
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
            text = "✓ BOOST COMPLETE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SuccessGreen,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Optimization Summary
        StatCard(
            title = "Cache Cleaned",
            value = String.format("%.2f MB", stats.getCacheSizeInMB()),
            icon = "💾"
        )

        StatCard(
            title = "Trash Removed",
            value = String.format("%.2f MB", stats.getTrashSizeInMB()),
            icon = "🗑️"
        )

        StatCard(
            title = "Temp Cleared",
            value = String.format("%.2f MB", stats.getTempSizeInMB()),
            icon = "📁"
        )

        StatCard(
            title = "Background Processes",
            value = "$backgroundProcesses",
            icon = "⚙️"
        )

        StatCard(
            title = "Total Optimization",
            value = String.format("%.2f MB", stats.getTotalOptimizationSizeInMB()),
            icon = "⚡",
            isHighlight = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Buttons
        Button(
            onClick = onBoostAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryNeon),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "BOOST AGAIN",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "BACK HOME",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: String,
    isHighlight: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isHighlight) SurfaceVariant else SurfaceDark,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlight) SecondaryNeon else ButtonNeon
                )
            }

            Text(text = icon, fontSize = 32.sp)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.aspectRatio(ratio: Float) =
    this.weight(1f)
