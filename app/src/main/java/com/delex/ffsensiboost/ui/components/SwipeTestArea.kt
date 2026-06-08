package com.delex.ffsensiboost.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delex.ffsensiboost.ui.theme.ButtonNeon
import com.delex.ffsensiboost.ui.theme.ErrorRed
import com.delex.ffsensiboost.ui.theme.SuccessGreen
import com.delex.ffsensiboost.ui.theme.SurfaceDark
import com.delex.ffsensiboost.ui.theme.TextPrimary
import com.delex.ffsensiboost.ui.theme.TextSecondary
import kotlin.math.abs

/**
 * Swipe Test Area Component
 * 
 * Implements Factor B of the Sensitivity Generation Algorithm.
 * Tracks user swipes to calculate velocity in pixels per millisecond.
 * 
 * The user performs 5 rounds of rapid upward swipes from the fire button.
 * Each round's velocity is tracked and used to determine the swipe performance profile.
 */
@Composable
fun SwipeTestArea(
    roundNumber: Int,
    onSwipeDetected: (swipeSpeedPixelsPerMs: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var touchDownTime by remember { mutableStateOf(0L) }
    var touchStartY by remember { mutableFloatStateOf(0f) }
    var touchEndY by remember { mutableFloatStateOf(0f) }
    var lastSwipeSpeed by remember { mutableFloatStateOf(0f) }
    var swipeDetected by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(SurfaceDark)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        // Record touch down
                        touchDownTime = System.currentTimeMillis()
                        touchStartY = offset.y
                        swipeDetected = false
                    },
                    onDragEnd = {
                        // Calculate swipe velocity
                        val touchUpTime = System.currentTimeMillis()
                        val timeDelta = (touchUpTime - touchDownTime).toFloat()
                        
                        if (timeDelta > 0) {
                            val distanceDelta = abs(touchEndY - touchStartY)
                            
                            // Calculate velocity in pixels per millisecond
                            val velocityPixelsPerMs = distanceDelta / timeDelta
                            
                            // Only count valid swipes (minimum distance and time constraints)
                            if (distanceDelta > 50f && timeDelta < 1000f && velocityPixelsPerMs > 0.1f) {
                                lastSwipeSpeed = velocityPixelsPerMs
                                swipeDetected = true
                                onSwipeDetected(velocityPixelsPerMs)
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        // Update current position during drag
                        touchEndY += dragAmount.y
                        change.consume()
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Round indicator
        Text(
            text = "ROUND $roundNumber / 5",
            fontSize = 14.sp,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Instructions
        Text(
            text = "Swipe Upward Rapidly",
            fontSize = 18.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "from the Fire Button",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Fire Button Simulator
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = if (swipeDetected) SuccessGreen else ButtonNeon,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔥",
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Speed display
        Text(
            text = if (swipeDetected) {
                String.format("Speed: %.3f px/ms", lastSwipeSpeed)
            } else {
                "Ready..."
            },
            fontSize = 16.sp,
            color = if (swipeDetected) SuccessGreen else TextSecondary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Progress bar showing which round
        RoundProgressIndicator(currentRound = roundNumber)

        Spacer(modifier = Modifier.height(30.dp))
    }
}

/**
 * Visual progress indicator showing rounds completed
 */
@Composable
fun RoundProgressIndicator(currentRound: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Progress",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Visual bars for rounds
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 4.dp)
                .background(SurfaceDark)
        ) {
            Box(
                modifier = Modifier
                    .size(
                        width = (200 * (currentRound / 5f)).dp,
                        height = 4.dp
                    )
                    .background(ButtonNeon)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$currentRound/5 Rounds",
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

/**
 * Extended SwipeTestArea with touch velocity calculation
 * Uses raw motion event data for precise velocity measurement
 */
@Composable
fun AdvancedSwipeTestArea(
    roundNumber: Int,
    onSwipeDetected: (swipeSpeedPixelsPerMs: Float, timestamp: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSpeed by remember { mutableFloatStateOf(0f) }
    var speedHistory by remember { mutableStateOf<List<Float>>(emptyList()) }
    var isActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(SurfaceDark)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        isActive = true
                        speedHistory = emptyList()
                    },
                    onDragEnd = {
                        isActive = false
                        
                        // Calculate average speed from history
                        if (speedHistory.isNotEmpty()) {
                            val avgSpeed = speedHistory.average().toFloat()
                            if (avgSpeed > 0.1f) {
                                onSwipeDetected(avgSpeed, System.currentTimeMillis())
                                currentSpeed = avgSpeed
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        // Calculate instantaneous speed
                        val instantSpeed = abs(dragAmount.y) / 16f // Assuming ~60fps updates
                        
                        speedHistory = speedHistory + instantSpeed
                        if (speedHistory.size > 10) {
                            speedHistory = speedHistory.drop(1)
                        }
                        
                        change.consume()
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ROUND $roundNumber / 5",
            fontSize = 14.sp,
            color = if (isActive) ButtonNeon else TextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (isActive) "Swiping..." else "Swipe Upward Rapidly",
            fontSize = 18.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Fire Button with visual feedback
        Box(
            modifier = Modifier
                .size(if (isActive) 90.dp else 80.dp)
                .background(
                    color = if (isActive) ErrorRed else ButtonNeon,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔥",
                fontSize = if (isActive) 48.sp else 40.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Real-time speed display
        Text(
            text = String.format("Instant: %.3f px/ms", speedHistory.lastOrNull() ?: 0f),
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = String.format("Average: %.3f px/ms", speedHistory.average()),
            fontSize = 14.sp,
            color = if (speedHistory.isNotEmpty()) SuccessGreen else TextSecondary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}
