# Android Architecture Project

This Android application demonstrates modern Android development practices using MVVM architecture, Clean Architecture principles, and various Android Jetpack components.

## Setup

### Unsplash API Key

To run this project, you need to obtain an API key from Unsplash:

1. Visit [Unsplash Developer Portal](https://unsplash.com/developers)
2. Create a developer account and register your application
3. Copy your Access Key
4. Add the following line to your `local.properties` file:
   ```
   unsplash.access.key=YOUR_ACCESS_KEY_HERE
   ```

**Note:** Never commit your `local.properties` file to version control. It's already included in `.gitignore`.

## Features

- Browse images from Unsplash
- Search for specific images
- Save favorite images
- View images offline
- Background sync with customizable intervals
- Modern Material 3 UI with Jetpack Compose

## Architecture

The project follows Clean Architecture principles with MVVM pattern:

- **Data Layer**: Handles data operations and contains repositories
- **Domain Layer**: Contains business logic and use cases
- **Presentation Layer**: Contains UI-related code (ViewModels and Compose UI)

## Technologies Used

- Kotlin
- Jetpack Compose
- Hilt for dependency injection
- Room for local database
- Retrofit for network requests
- WorkManager for background tasks
- DataStore for settings
- Coil for image loading

## Building and Running

1. Clone the repository
2. Add your Unsplash API key to `local.properties`
3. Build and run the project using Android Studio 