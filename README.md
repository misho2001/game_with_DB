========================================
           JavaFX Game App
========================================

ABOUT THE APP
-------------
This is a desktop application built with JavaFX and MySQL.
It features a complete user authentication system and a simple, 
timing-based mini-game.This app uses TiDB cloud to store date online.

KEY FEATURES
------------
- Account System: User Login and Registration with database support.
- Timing Game: 
  * Generates a random target number when you start.
  * A hidden timer runs in the background.
  * Tap the button to stop the timer and try to hit your target time.
- High Score Tracking: Automatically saves your final score and time to the MySQL database.

HOW IT WORKS
------------
1. Launch the app and register a new account or log in.
2. The game panel opens automatically upon login.
3. Tap "STOP TIMER" when you think enough time has passed.
4. Your random number and exact elapsed time are saved!
