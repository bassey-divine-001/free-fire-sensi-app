package com.delex.ffsensiboost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delex.ffsensiboost.data.model.DeviceSpecifications
import com.delex.ffsensiboost.data.model.SensitivityConfiguration
import com.delex.ffsensiboost.data.model.SwipeTestResult
import com.delex.ffsensiboost.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * ViewModel for Sensitivity Generation System
 * Implements the core algorithm combining:
 * - Factor A: Device Hardware Metrics
 * - Factor B: User Swipe Speed Profile
 * - Factor C: Arithmetic Calculation Engine
 *
 * Algorithm produces optimized Free Fire sensitivity settings.
 */
@HiltViewModel
class SensitivityViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    // State flows for reactive updates
    private val _deviceSpecs = MutableStateFlow<DeviceSpecifications?>(null)
    val deviceSpecs: StateFlow<DeviceSpecifications?> = _deviceSpecs.asStateFlow()

    private val _swipeResults = MutableStateFlow<List<Float>>(emptyList())
    val swipeResults: StateFlow<List<Float>> = _swipeResults.asStateFlow()

    private val _generatedConfig = MutableStateFlow<SensitivityConfiguration?>(null)
    val generatedConfig: StateFlow<SensitivityConfiguration?> = _generatedConfig.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _testProgress = MutableStateFlow(0)
    val testProgress: StateFlow<Int> = _testProgress.asStateFlow()

    /**
     * FACTOR A: Scan device specifications from Android system
     */
    fun scanDeviceSpecifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val specs = deviceRepository.getDeviceSpecifications()
                _deviceSpecs.value = specs
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * FACTOR B: Process swipe test results
     * Called after user completes the swipe test
     */
    fun processSwipeTestRound(speedPixelsPerMs: Float, roundNumber: Int) {
        val currentSpeeds = _swipeResults.value.toMutableList()
        
        // Ensure list has enough capacity
        while (currentSpeeds.size <= roundNumber) {
            currentSpeeds.add(0f)
        }
        
        // Add speed to the appropriate round
        currentSpeeds[roundNumber] = speedPixelsPerMs
        _swipeResults.value = currentSpeeds
        
        // Update progress
        _testProgress.value = ((roundNumber + 1) * 20).coerceAtMost(100)
    }

    /**
     * Calculate swipe test statistics from all rounds
     */
    private fun calculateSwipeStatistics(): SwipeTestResult {
        val speeds = _swipeResults.value
        
        if (speeds.isEmpty()) {
            return SwipeTestResult(
                roundSpeeds = emptyList(),
                averageSpeed = 0f,
                consistencyScore = 0f,
                peakSpeed = 0f,
                testDuration = 0L
            )
        }

        // Filter out zero values
        val validSpeeds = speeds.filter { it > 0f }
        
        if (validSpeeds.isEmpty()) {
            return SwipeTestResult(
                roundSpeeds = speeds,
                averageSpeed = 0f,
                consistencyScore = 0f,
                peakSpeed = 0f,
                testDuration = 0L
            )
        }

        val average = validSpeeds.average().toFloat()
        val peak = validSpeeds.maxOrNull() ?: 0f

        // Calculate consistency score (0-100)
        // Lower standard deviation = higher consistency
        val variance = validSpeeds.map { (it - average).pow(2) }.average()
        val standardDeviation = variance.pow(0.5f)
        
        // Normalize standard deviation to 0-100 score (lower std = higher score)
        val consistencyScore = (100f - (standardDeviation / average * 100f)).coerceIn(0f, 100f)

        return SwipeTestResult(
            roundSpeeds = speeds,
            averageSpeed = average,
            consistencyScore = consistencyScore,
            peakSpeed = peak,
            testDuration = 5000L  // Standard 5-round test
        )
    }

    /**
     * FACTOR C: Core Arithmetic Algorithm
     * Combines device specifications + swipe metrics to generate sensitivity settings
     * 
     * Algorithm Logic:
     * - High-end device + fast swipes = Lower sensitivity (precise aiming)
     * - Budget device + slower swipes = Higher sensitivity (compensate for friction)
     */
    fun generateSensitivityConfiguration() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val specs = _deviceSpecs.value ?: run {
                    // If specs not yet loaded, scan first
                    scanDeviceSpecifications()
                    _deviceSpecs.value ?: return@launch
                }

                val swipeStats = calculateSwipeStatistics()
                
                // Calculate normalized factors (0-100 scale)
                val devicePerformanceScore = specs.getPerformanceRating()      // 0-100
                val swipePerformanceScore = swipeStats.getNormalizedSwipeFactor() // 0-100

                // Base sensitivity multiplier calculation
                // Higher device performance = lower base sensitivity needed
                // Higher swipe speed = lower sensitivity needed
                val deviceFactor = 1.0f - (devicePerformanceScore / 100f * 0.5f)  // 0.5-1.0
                val swipeFactor = 1.0f - (swipePerformanceScore / 100f * 0.5f)    // 0.5-1.0

                // Combined sensitivity multiplier (weighted average)
                val combinedMultiplier = (deviceFactor * 0.4f + swipeFactor * 0.6f)

                // Apply multiplier to base sensitivity values
                val generalBase = 100
                val redDotBase = 95
                val scope2xBase = 85
                val scope4xBase = 75
                val sniperBase = 60
                val freeLookBase = 110

                val config = SensitivityConfiguration(
                    generalSensitivity = (generalBase * combinedMultiplier).roundToInt().coerceIn(1, 200),
                    redDotSensitivity = (redDotBase * combinedMultiplier).roundToInt().coerceIn(1, 200),
                    scope2xSensitivity = (scope2xBase * combinedMultiplier).roundToInt().coerceIn(1, 200),
                    scope4xSensitivity = (scope4xBase * combinedMultiplier).roundToInt().coerceIn(1, 200),
                    sniperScopeSensitivity = (sniperBase * combinedMultiplier).roundToInt().coerceIn(1, 200),
                    freeLookSensitivity = (freeLookBase * combinedMultiplier).roundToInt().coerceIn(1, 200),
                    fireButtonSize = calculateFireButtonSize(specs),
                    recommendedDPI = calculateOptimalDPI(specs),
                    deviceRating = devicePerformanceScore,
                    swipeFactor = swipePerformanceScore
                )

                _generatedConfig.value = config
                _testProgress.value = 100

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Calculate optimal Fire Button size based on screen resolution and device capabilities
     * Returns percentage (50-150%)
     */
    private fun calculateFireButtonSize(specs: DeviceSpecifications): Int {
        // Base calculation on screen diagonal
        val diagonal = kotlin.math.sqrt(
            (specs.screenResolutionWidth * specs.screenResolutionWidth +
                    specs.screenResolutionHeight * specs.screenResolutionHeight).toDouble()
        ).toFloat()

        // Smaller screens may need larger buttons for easier tapping
        val sizeAdjustment = when {
            diagonal < 4.5f -> 130  // Small screen
            diagonal < 5.5f -> 110  // Medium screen
            diagonal < 6.5f -> 100  // Standard large screen
            else -> 90              // Extra large screen
        }

        return sizeAdjustment.coerceIn(50, 150)
    }

    /**
     * Calculate custom DPI setting based on device resolution and performance
     * Higher DPI = Smaller UI elements (more screen real estate for game)
     */
    private fun calculateOptimalDPI(specs: DeviceSpecifications): Int {
        // Base on current device DPI but optimize for gaming
        val baseDPI = specs.screenDensityDPI

        // For high-refresh displays and high-end devices, can afford lower DPI
        val performanceBonus = if (specs.screenRefreshRate >= 120 && specs.getPerformanceRating() >= 75f) {
            -20
        } else if (specs.screenRefreshRate >= 90 && specs.getPerformanceRating() >= 60f) {
            -10
        } else {
            0
        }

        return (baseDPI + performanceBonus).coerceIn(160, 640)
    }

    /**
     * Reset the ViewModel state for a new test
     */
    fun resetTest() {
        _swipeResults.value = emptyList()
        _testProgress.value = 0
        _generatedConfig.value = null
    }

    /**
     * Get formatted device info string
     */
    fun getFormattedDeviceInfo(): String {
        val specs = _deviceSpecs.value ?: return "Device not scanned"
        
        return """
            ${specs.deviceManufacturer} ${specs.deviceModel}
            Android ${specs.androidVersion} (API ${specs.apiLevel})
            RAM: ${specs.totalRAM / 1_000_000_000}GB / ${specs.availableRAM / 1_000_000_000}GB Available
            CPU: ${specs.processorCores} Cores
            Display: ${specs.screenResolutionWidth}x${specs.screenResolutionHeight} @ ${specs.screenRefreshRate}Hz
            Performance Rating: ${String.format("%.1f", specs.getPerformanceRating())}/100
        """.trimIndent()
    }

    /**
     * Get formatted swipe test results
     */
    fun getFormattedSwipeResults(): String {
        val speeds = _swipeResults.value
        if (speeds.isEmpty()) return "No swipe data"

        val validSpeeds = speeds.filter { it > 0f }
        if (validSpeeds.isEmpty()) return "No valid swipe data"

        val average = validSpeeds.average()
        val stats = calculateSwipeStatistics()

        return buildString {
            append("Swipe Test Results:\n")
            append("─────────────────\n")
            speeds.forEachIndexed { index, speed ->
                if (speed > 0f) {
                    append("Round ${index + 1}: ${String.format("%.2f", speed)} px/ms\n")
                }
            }
            append("─────────────────\n")
            append("Average: ${String.format("%.2f", average)} px/ms\n")
            append("Peak: ${String.format("%.2f", stats.peakSpeed)} px/ms\n")
            append("Consistency: ${String.format("%.1f", stats.consistencyScore)}/100")
        }
    }
}
