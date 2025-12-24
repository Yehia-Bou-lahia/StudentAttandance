# Student Attendance App

A modern Android application for tracking student attendance with a beautiful, intuitive UI built using Jetpack Compose.

## Features

### 📊 Dashboard
- **Overall Attendance Overview**: Visual representation of attendance percentage with animated circular progress indicator
- **Attendance Statistics**: Quick view of total, present, and absent classes
- **Attendance Status Cards**: Dynamic cards showing attendance warnings or success messages
- **Upcoming Classes**: List of scheduled classes with room and duration information
- **Check-in Status**: Visual indicators for classes already checked into

### 🔐 Login Page
- Clean and modern login interface
- Student ID/Email authentication
- Password visibility toggle
- Forgot password functionality
- Sign-up option for new students

### 🧭 Bottom Navigation
- **Home**: Dashboard with attendance overview
- **Calendar**: Schedule and calendar view
- **Scan**: Prominent QR code scanner with glowing effect for attendance check-in
- **History**: Attendance history and records
- **Profile**: User profile and settings

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: API 24 (Android 7.0)
- **Architecture**: Modern Android development practices

## Project Structure

```
app/src/main/java/com/example/studentattandance/
├── MainActivity.kt              # Main activity entry point
├── LoginPage.kt                 # Student login screen
├── Dashboard.kt                 # Main dashboard with attendance info
├── BottomNavigationBar.kt       # Bottom navigation component
├── AttendanceCard.kt            # Attendance status cards
└── ui/theme/                    # Theme and color definitions
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

## Color Scheme

The app uses a dark navy theme with accent colors:

- **DeepNavy**: `#0C1926` - Primary background
- **CardNavy**: `#12263A` - Card backgrounds
- **AccentBlue**: `#0F9CF3` - Primary accent color
- **TextPrimary**: `#E6EEF7` - Primary text
- **TextSecondary**: `#9EB3C7` - Secondary text
- **SuccessGreen**: `#2ED47A` - Success indicators
- **MutedRed**: `#E45B78` - Warning/error indicators

## Key Components

### Dashboard
The dashboard displays:
- Personalized greeting with student name
- Overall attendance percentage with animated progress ring
- Statistics breakdown (Total, Present, Absent)
- Attendance status cards (Success/Warning/Exclusion)
- Upcoming class schedule

### Bottom Navigation Bar
Features a unique floating QR scanner button in the center with:
- Blue gradient background
- Glowing effect for prominence
- Custom icon colors (Home icon retains original color)
- Smooth selection animations

### Login Screen
Includes:
- Student ID/Email input field
- Password field with visibility toggle
- Forgot password link


## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

## Requirements

- Android Studio Hedgehog or newer
- JDK 17 or higher
- Android SDK 34
- Gradle 8.0+

## Future Enhancements

- [ ] QR code scanning functionality
- [ ] Real-time attendance tracking
- [ ] Push notifications for upcoming classes
- [ ] Detailed attendance reports
- [ ] Calendar integration
- [ ] Profile customization


## License

This project is developed for educational purposes.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

**Note**: This app is currently in development. Some features may require backend integration for full functionality.
