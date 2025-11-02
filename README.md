# Minesweeper Android App

A classic Minesweeper game implementation for Android, built with Java.

## Features

- **Three Main Screens:**
  1. Title/Instructions Screen - Welcome screen with game instructions
  2. Settings Screen - Customize game board and colors
  3. Game Screen - Play the Minesweeper game

- **Customizable Settings:**
  - Board size: 5x5 to 10x10
  - Mine density: 10%, 15%, or 20%
  - Color themes for all cell types (5 options each)

- **Game Features:**
  - Tap cells to reveal
  - Long-press to flag/unflag suspected mines
  - Auto-reveal surrounding cells when clicking cells with 0 adjacent mines
  - Win/lose detection with Toast notifications

## Setup

1. Open this project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or connected device

## Requirements

- Android Studio (latest version)
- Android SDK 24 or higher
- Gradle 8.2 or compatible

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/minesweeper/
│   │   ├── MainActivity.java          # Title/Instructions screen
│   │   ├── SettingsActivity.java      # Settings screen
│   │   ├── GameActivity.java          # Game screen
│   │   └── GameBoard.java             # Game logic
│   ├── res/
│   │   ├── layout/                    # XML layouts
│   │   └── values/                    # Strings, colors, themes
│   └── AndroidManifest.xml
└── build.gradle
```

## Game Rules

- Tap a cell to reveal it
- If it's a mine, you lose
- If it shows a number, that's how many mines are adjacent
- If it shows nothing (0), all adjacent cells are automatically revealed
- Long-press to flag/unflag a suspected mine
- Reveal all non-mine cells to win

