Kotlin Sudoku – Mastering OOP & SOLID Principles
Welcome! This project is a Sudoku application built from the ground up using Kotlin. While the end goal was a functional game, the primary objective was to dive deep into the Kotlin ecosystem and apply industry-standard design patterns, specifically OOP and SOLID principles.

🎯 Project Goals
The focus of this repository is "Code Quality over Complexity." I used this project to:

Transition to Kotlin: Leveraging features like Null Safety, Data Classes, and concise syntax.

Implement OOP: Focusing on encapsulation, inheritance, and polymorphism to create a modular game engine.

Apply SOLID: Ensuring the codebase is maintainable, scalable, and easy to read.

🧩 SOLID Principles Applied
I used this project as a sandbox to practice writing "Clean Code." Here is how the principles are reflected in the architecture:

Single Responsibility (SRP): Each class has one job. For example, the SudokuLogic handles the game rules, while the UI components focus strictly on rendering and user input.

Open/Closed (OCP): The game logic is designed to be open for extension (e.g., adding a "Hint" system or a "New Difficulty" level) without modifying the core validation engine.

Liskov Substitution (LSP): Any implementation of a game listener or state handler can be swapped without breaking the application logic.

Interface Segregation (ISP): I avoided "fat interfaces." Classes only implement the specific methods they need to function.

Dependency Inversion (DIP): High-level modules (the game loop) depend on abstractions (interfaces) rather than low-level details (concrete UI implementations).

🚀 Key Features
Dynamic Board Generation: Logic to create and validate Sudoku puzzles.

Interactive UI: Clean user interface built with [Swing/Compose - specify if applicable].

Real-time Validation: Instant feedback on incorrect moves.

Clean Architecture: Separation of concerns between the data, logic, and presentation layers.

🛠 Tech Stack
Language: Kotlin 1.9+

Architecture: Model-View-ViewModel (MVVM) / Observer Pattern

Build System: Gradle
