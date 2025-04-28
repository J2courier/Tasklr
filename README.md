# Tasklr

Tasklr is a Java-based desktop application designed to help users organize tasks and enhance learning through flashcards and quizzes. It combines task management functionality with study tools to create a comprehensive productivity platform.

## Features

### Task Management
- Create, track, and manage tasks
- Real-time task status updates
- Task categorization and filtering
- Visual task statistics and progress tracking

### Study Tools
- Flashcard creation and management
- Quiz system with multiple attempt tracking
- Study progress statistics
- Performance analytics

### User Experience
- Modern, intuitive user interface
- Real-time data synchronization
- Secure user authentication
- Account recovery system
- Personalized dashboard with statistics

## Technical Stack

- **Language:** Java 22
- **UI Framework:** Swing/AWT
- **Database:** MySQL (via XAMPP)
- **Build System:** Apache Ant
- **IDE Support:** NetBeans, VS Code

### Dependencies
- MySQL Connector J 9.2.0
- JCalendar 1.4
- Custom JForm components

## Installation

1. Ensure you have Java 22 or later installed
2. Install XAMPP with MySQL
3. Clone the repository
4. Import the project into NetBeans or your preferred IDE
5. Configure the database:
   - Create a database named `tasklrdb`
   - Use username: `JFCompany` (no password)
   - Import the provided database schema

## Development Setup

1. Configure your IDE to use Java 22
2. Add required libraries from the `lib` directory
3. Set up XAMPP:
   - Start Apache and MySQL services
   - Ensure the database is accessible

## Building

```bash
# Using Ant
ant clean
ant compile
ant jar
```

## Running

```bash
# Using Java
java -jar dist/Tasklr.jar
```

## Database Schema

The application uses the following main tables:
- `users`: User account management
- `tasks`: Task tracking and management
- `sessions`: User session handling
- `flashcard_sets`: Flashcard organization
- `flashcards`: Individual flashcard data
- `quiz_attempts`: Quiz performance tracking
- `quiz_statistics`: Aggregated quiz performance data

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Authors

[Jherson Bartolay]
