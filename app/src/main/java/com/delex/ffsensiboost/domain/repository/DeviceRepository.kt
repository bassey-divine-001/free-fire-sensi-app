package com.delex.ffsensiboost.domain.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.delex.ffsensiboost.data.model.DeviceSpecifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Repository for extracting device specifications from Android system.
 * Implements Factor A of the Sensitivity Generation Algorithm.
 */
class DeviceRepository(private val context: Context) {

    /**
     * Extract comprehensive device specifications from Android OS
     */
    suspend fun getDeviceSpecifications(): DeviceSpecifications = withContext(Dispatchers.Default) {
        val deviceModel = Build.MODEL
        val deviceManufacturer = Build.MANUFACTURER
        val androidVersion = Build.VERSION.SDK_INT
        val apiLevel = Build.VERSION.SDK_INT
        
        val (totalRAM, availableRAM) = getRAMInfo()
        val processorCores = Runtime.getRuntime().availableProcessors()
        
        val (screenWidth, screenHeight, screenRefreshRate, screenDensity, dpi) = getScreenMetrics()
        
        val deviceEraYear = estimateDeviceEra(apiLevel)
        
        DeviceSpecifications(
            deviceModel = deviceModel,
            deviceManufacturer = deviceManufacturer,
            androidVersion = androidVersion,
            apiLevel = apiLevel,
            totalRAM = totalRAM,
            availableRAM = availableRAM,
            processorCores = processorCores,
            screenResolutionWidth = screenWidth,
            screenResolutionHeight = screenHeight,
            screenRefreshRate = screenRefreshRate,
            deviceEraYear = deviceEraYear,
            isDPICustomizable = true,
            screenDensity = screenDensity,
            screenDensityDPI = dpi
        )
    }

    /**
     * Extract RAM information from system
     * Returns pair of (totalRAM in bytes, availableRAM in bytes)
     */
    private fun getRAMInfo(): Pair<Long, Long> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        return Pair(memInfo.totalMemory, memInfo.availMem)
    }

    /**
     * Extract screen metrics and display information
     * Returns tuple of (width, height, refreshRate, density, dpi)
     */
    private fun getScreenMetrics(): Tuple5<Int, Int, Float, Float, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val density = displayMetrics.density
        val dpi = displayMetrics.densityDpi
        
        // Get refresh rate - available in API 30+
        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.refreshRate
        } else {
            60f
        }
        
        return Tuple5(width, height, refreshRate, density, dpi)
    }

    /**
     * Estimate device era/year based on API level and model patterns
     */
    private fun estimateDeviceEra(apiLevel: Int): Int {
        return when {
            apiLevel >= 33 -> 2023  // Android 13+
            apiLevel >= 31 -> 2022  // Android 12
            apiLevel >= 29 -> 2021  // Android 10
            apiLevel >= 28 -> 2020  // Android 9
            apiLevel >= 26 -> 2019  // Android 8
            apiLevel >= 24 -> 2018  // Android 7
            else -> 2017
        }
    }

    /**
     * Get device info string for display purposes
     */
    suspend fun getDeviceInfoString(): String = withContext(Dispatchers.Default) {
        val specs = getDeviceSpecifications()
        
        """
        Device: ${specs.deviceManufacturer} ${specs.deviceModel}
        Android: ${specs.androidVersion} (API ${specs.apiLevel})
        RAM: ${specs.totalRAM / 1_000_000_000}GB
        Cores: ${specs.processorCores}
        Screen: ${specs.screenResolutionWidth}x${specs.screenResolutionHeight} @ ${specs.screenRefreshRate}Hz
        """.trimIndent()
    }
}

/**
 * Helper data class for tuple return values
 */
data class Tuple5<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
