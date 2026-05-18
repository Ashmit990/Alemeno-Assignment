# Alemeno Frontend Internship Assignment

## Approach: Custom Marker Detection & Extraction

### 1. Marker Design & Analysis
Used the provided custom markers (Marker1 and Marker2). These markers are black and white squares with internal patterns. The detection logic focuses on finding square contours with specific aspect ratios and internal density constraints.

### 2. Implementation Overview
- **Framework**: React Native with TypeScript.
- **Camera**: `react-native-vision-camera` for low-latency live feed.
- **Processing Engine**: Native Java code with **OpenCV 4.x** for high efficiency.
- **Accuracy**: Perspective transformation used to correct skew and orientation.

### 3. Detection Logic (Native Module)
- **Grayscale & Blur**: Reduce noise.
- **Canny Edge Detection**: Locate boundaries.
- **Contour Analysis**: Find 4-pointed polygons (approxPolyDP).
- **Perspective Transform**: Warp the detected area into a fixed 300x300px square.
- **Validation**: Ensures the detected shape is a square and satisfies area constraints to prevent false positives.

### 4. Constraints Adherence
- **Camera Resolution**: Configured to capture between 2000x2000 and 3000x3000px.
- **Marker Output**: Exactly 300x300px.
- **Count**: Captures 20 unique frames containing the marker.
- **Speed**: Optimized native execution ensures results under 3000ms.

### 5. Setup Instructions
1. Clone the repository.
2. Run `npm install`.
3. Open `android/` in Android Studio to sync Gradle.
4. Run `npx react-native run-android`.
