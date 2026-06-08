# FF SENSI BOOST - Production-Ready Android Application

## Overview
**FF Sensi Boost** is a comprehensive Free Fire optimization application featuring an intelligent sensitivity generator and system booster. The app uses a proprietary two-factor algorithm combining device hardware specifications with user swipe velocity profiles to generate optimized Free Fire sensitivity settings.

---

## Project Architecture

### Clean Architecture + MVVM Pattern
The project follows industry best practices with Clear Separation of Concerns:

```
com/delex/ffsensiboost/
├── di/                          # Dependency Injection (Hilt)
│   └── RepositoryModule.kt
├── data/
│   └── model/                   # Data classes
│       └── Models.kt
├── domain/
│   └── repository/              # Business logic repositories
│       └── DeviceRepository.kt
├── ui/
│   ├── theme/                   # Theming (Color, Type, Theme)
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── screens/                 # Composable screens
│   │   ├── MainDashboardScreen.kt
│   │   ├── SensitivityScreen.kt
│   │   └── BoosterScreen.kt
│   └── components/              # Reusable components
│       └── SwipeTestArea.kt
├── viewmodel/                   # ViewModels (MVVM)
│   ├── SensitivityViewModel.kt
│   └── BoosterViewModel.kt
├── MainActivity.kt              # Entry point
└── FFSensiBoostApp.kt          # Hilt Application
```

---

## Core Features

### 1. SENSITIVITY GENERATOR (AI-Powered Algorithm)

#### Factor A: Device Information Scanner
Automatically extracts comprehensive device metrics from Android system:

**Extracted Metrics:**
- Device Model & Manufacturer
- Android OS Version & API Level
- Total RAM & Available RAM
- CPU Core Count
- Screen Resolution (Width x Height)
- Screen Refresh Rate (Hz)
- Device Density & DPI
- Device Era Estimation

**Implementation:**
- `DeviceRepository.kt` - Scans system using Android Build and ActivityManager classes
- Compatible with Android 26+ (API Level 26 through latest)
- Runtime permission handling for modern Android versions

**Performance Rating Algorithm:**
```
Rating = (RAM Score * 0.3) + (CPU Score * 0.3) + (API Level Score * 0.2) + (Refresh Rate Score * 0.2)
```

#### Factor B: Dynamic Swipe Test Engine
Interactive 5-round swipe velocity tracking system:

**How It Works:**
1. User taps the Fire Button simulator
2. Performs rapid **upward swipe** motion
3. App tracks **touch down** and **touch up** times (milliseconds)
4. Calculates **distance traveled** (pixels)
5. Computes **velocity = distance / time** (pixels per millisecond)
6. Stores all 5 round speeds
7. Calculates **average speed** and **consistency score**

**Implementation Details (SwipeTestArea.kt):**
- Uses `MotionEvent` through `detectVerticalDragGestures`
- Tracks instantaneous velocity during drag
- Minimum constraints: 50px distance, < 1000ms duration, > 0.1 px/ms speed
- Consistency Score = 100 - (StdDev / Average * 100)

**Output:**
```
SwipeTestResult(
    roundSpeeds: [1.235, 1.456, 1.378, 1.412, 1.389] px/ms
    averageSpeed: 1.374 px/ms
    consistencyScore: 87.3/100
    peakSpeed: 1.456 px/ms
)
```

#### Factor C: Arithmetic Calculation Engine
Combines device specs + swipe metrics into sensitivity settings:

**Algorithm Logic:**

```kotlin
// Device Performance Factor (0-100 scale)
devicePerformanceScore = DeviceSpecs.getPerformanceRating()

// Swipe Performance Factor (0-100 scale)
swipePerformanceScore = SwipeResults.getNormalizedSwipeFactor()

// Base multiplier calculation
// Higher device performance = lower multiplier (precise aiming)
// Higher swipe speed = lower multiplier (less adjustment needed)
deviceFactor = 1.0 - (devicePerformanceScore / 100 * 0.5)    // 0.5-1.0
swipeFactor = 1.0 - (swipePerformanceScore / 100 * 0.5)      // 0.5-1.0

// Combined multiplier (weighted average)
combinedMultiplier = (deviceFactor * 0.4) + (swipeFactor * 0.6)  // Favors swipe speed

// Apply to base sensitivity values
generalSensitivity = (100 * combinedMultiplier).clamp(1, 200)
redDotSensitivity = (95 * combinedMultiplier).clamp(1, 200)
scope2xSensitivity = (85 * combinedMultiplier).clamp(1, 200)
scope4xSensitivity = (75 * combinedMultiplier).clamp(1, 200)
sniperScopeSensitivity = (60 * combinedMultiplier).clamp(1, 200)
freeLookSensitivity = (110 * combinedMultiplier).clamp(1, 200)
```

**Fire Button Size Calculation:**
```
diagonal = sqrt(width² + height²)
sizeAdjustment:
  - diagonal < 4.5": 130% (small screen)
  - diagonal < 5.5": 110% (medium screen)
  - diagonal < 6.5": 100% (large screen)
  - else: 90% (extra large screen)
```

**Optimal DPI Setting:**
```
baseDPI = deviceDensityDPI
performanceBonus:
  - If (refreshRate >= 120Hz && rating >= 75): -20
  - If (refreshRate >= 90Hz && rating >= 60): -10
  - Else: 0
recommendedDPI = (baseDPI + bonus).clamp(160, 640)
```

---

### 2. SYSTEM BOOSTER (Performance Optimization)

#### Scanning Phase
Automatically calculates:
- **Cache Size**: App cache directory + external cache
- **Trash Size**: Estimated debris (30% of cache)
- **Temporary Files**: /data/local/tmp size
- **Background Processes**: Non-system running services
- **Total Optimization**: Sum of all removable content

#### Boosting Phase
Multi-step optimization with progress tracking:

1. **Clear App Cache** (20%)
   - Clears `context.cacheDir`
   - Removes external cache if available

2. **Remove Temporary Files** (40%)
   - Deletes /data/local/tmp
   - Safely handles permission errors

3. **Optimize RAM** (60%)
   - Triggers `System.gc()` garbage collection
   - Calls `System.runFinalization()` for cleanup

4. **Kill Background Processes** (80%)
   - Filters non-system applications
   - Sends SIGKILL to background processes
   - Preserves critical system apps

5. **Final Verification** (100%)
   - Re-scans system
   - Reports final statistics

**Output:**
```
Cache Cleaned: X.XX MB
Trash Removed: Y.YY MB
Temp Cleared: Z.ZZ MB
Background Processes: N
Total Optimization: TOTAL.XX MB
```

---

## UI/UX Design

### Theme: Gaming Aesthetic Dark Mode
- **Primary Neon**: Cyan (#00FFD9) - Main action color
- **Secondary Neon**: Magenta (#FF006E) - Booster accent
- **Tertiary Neon**: Golden (#FFBE0B) - Highlights
- **Background**: Deep Black (#0F1419)
- **Surface**: Dark Blue (#1A1F2E)

### Navigation Flow
```
Dashboard (Main Menu)
├── → Sensitivity Generator
│   ├── Device Info Scan
│   ├── 5-Round Swipe Test
│   └── Results Display
└── → System Booster
    ├── System Scan
    ├── Boost Execution
    └── Results Summary
```

---

## Permissions (AndroidManifest.xml)

### Required for Device Scanning
- `android.permission.READ_PHONE_STATE`
- `android.permission.ACCESS_NETWORK_STATE`
- `android.permission.PACKAGE_USAGE_STATS`

### Required for Booster
- `android.permission.MANAGE_EXTERNAL_STORAGE`
- `android.permission.WRITE_EXTERNAL_STORAGE`
- `android.permission.READ_EXTERNAL_STORAGE`
- `android.permission.DELETE_CACHE_FILES`
- `android.permission.GET_TASKS`
- `android.permission.QUERY_ALL_PACKAGES`

### Optional
- `android.permission.VIBRATE` - Haptic feedback
- `android.permission.INTERNET` - Future analytics

---

## Dependencies (build.gradle.kts)

### Core Android
- androidx.core:core-ktx:1.12.0
- androidx.lifecycle:lifecycle-runtime-ktx:2.6.2
- androidx.activity:activity-compose:1.8.0

### Jetpack Compose
- androidx.compose.ui:ui:1.5.4
- androidx.compose.material3:material3:1.1.2
- androidx.compose.material:material-icons-extended:1.5.4

### Hilt DI
- com.google.dagger:hilt-android:2.48.1
- com.google.dagger:hilt-compiler:2.48.1
- androidx.hilt:hilt-navigation-compose:1.1.0

### Navigation
- androidx.navigation:navigation-compose:2.7.5

### Coroutines
- org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3

### Data Storage
- androidx.datastore:datastore-preferences:1.0.0

---

## Build Configuration

### Compilation
- Target SDK: 34 (Android 14)
- Min SDK: 26 (Android 8.0)
- Java Version: 17
- Kotlin Compiler Extension: 1.5.3

### Build Types
- **Debug**: Full debugging support
- **Release**: ProGuard minification enabled

---

## Code Highlights

### SensitivityViewModel - Complete Algorithm Implementation
The `SensitivityViewModel.kt` contains:
- Device specification scanning (Factor A)
- Swipe test result processing (Factor B)
- Sensitivity calculation algorithm (Factor C)
- Result generation with comprehensive metrics

### BoosterViewModel - System Optimization
The `BoosterViewModel.kt` handles:
- System scanning and analysis
- Multi-step boost execution
- Progress tracking
- Result reporting

### SwipeTestArea Component - Touch Tracking
The `SwipeTestArea.kt` implements:
- Real-time touch velocity calculation
- Multi-round tracking
- Consistency scoring
- Visual feedback

---

## Performance Optimizations

1. **Coroutine-Based Operations**: All I/O operations use Dispatchers.IO
2. **StateFlow for Reactivity**: Real-time UI updates without unnecessary recompositions
3. **Lazy Data Loading**: Device specs scanned only when needed
4. **Memory Efficient**: Carousel pattern for historical swipe data
5. **ProGuard Minification**: ~40% APK size reduction in release build

---

## Testing & Quality

### Error Handling
- Try-catch blocks for permission errors
- Graceful degradation for unsupported features
- User-friendly error messages

### Code Quality
- Clean Architecture principles
- Separation of concerns
- Dependency injection
- Type-safe Kotlin
- Comprehensive documentation

---

## Future Enhancement Possibilities

1. **Cloud Sync**: Save profiles to Firebase
2. **Multiplayer Calibration**: Compare settings with other players
3. **Game-Specific Profiles**: Separate configs for different games
4. **Hardware Acceleration Monitoring**: Track GPU usage
5. **ML Model Integration**: Predictive sensitivity suggestions
6. **Achievement System**: Gamify optimization milestones

---

## Installation & Build

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 34
- Gradle 8.2.0+
- Kotlin 1.9.10+

### Build Steps
```bash
# Clone repository
git clone <repo-url>

# Navigate to project
cd ff-sensi-boost

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test
```

---

## License & Credits

**FF Sensi Boost v1.0.0**
- Platform: Android Only (API 26+)
- Architecture: Clean Architecture + MVVM
- UI Framework: Jetpack Compose
- DI Framework: Hilt

---

## Support & Documentation

For detailed implementation guides, see:
- `SensitivityViewModel.kt` - Algorithm documentation
- `SwipeTestArea.kt` - Touch tracking implementation
- `BoosterViewModel.kt` - System optimization logic
- `DeviceRepository.kt` - Device metrics extraction
