# Smart Scheduler Pro - Task Management Application


### Table of Contents

_Overview_

_Features_

_Installation_

_Usage_

_Technical Details_

_Project Structure_

_Contributing_

_License_

## Overview

Smart Scheduler Pro is a comprehensive Java-based task management application designed to help users organize their tasks, deadlines, and schedules efficiently. Built with Java Swing and modern FlatLaf for the GUI, and JSON for data persistence, this application offers a polished, intuitive interface with powerful task management capabilities.

## Features

### Core Task Management

✅ Create, edit, and delete tasks with titles, descriptions, and due dates
✅ Set task priorities (High, Medium, Low) with color coding
✅ Organize tasks by customizable categories with color coding
✅ Mark tasks as complete/incomplete
✅ Add subtasks to break down complex tasks
✅ Tag system for additional organization

### Advanced Features

🔄 Recurring tasks with flexible scheduling options
📅 Multiple views:
Table view with filtering/sorting
Calendar view (monthly)
Schedule view (weekly overview)
Completed tasks view
🔍 Powerful filtering and searching capabilities
🎨 Modern UI with FlatLaf theming and custom styling

### Data Management

💾 Automatic saving of tasks and settings
🔄 JSON-based data storage for easy backup and transfer
🛡️ Defensive data handling to prevent corruption

## Installation

### Prerequisites

* Java JDK 17 or later
* Maven (for building from source)
* Running the Application

### Option 1: Download Pre-built JAR

Download the latest release JAR file from the Releases page

Run the application with:
``` bash
* java -jar smart-scheduler-pro.jar
```
### Option 2: Build from Source

Clone the repository:

``` bash
git clone https://github.com/yourusername/scheduler.git
```
* Navigate to the project directory:

``` bash
cd smart-scheduler-pro
```
* Build the project with Maven:
``` bash
mvn clean package
```
* Run the application:
``` bash
java -jar target/smart-scheduler-1.0.0.jar
```

## Usage

### Main Interface

The application features a tabbed interface with four main views:

* Tasks: View and manage all active tasks in a sortable table with priority and category coloring
* Calendar: See your tasks in a monthly calendar layout
* Schedule: Weekly overview showing tasks for the next N weeks (configurable)
* Completed: Review and manage completed tasks

### Creating a Task

* Click Add Task in the Tasks view
* Fill in task details:
* Title (required)
* Description (optional)
* Due date and time
* Priority level (color-coded)
* Category (color-coded)
* Click Save to add the task

### Task Actions

* Edit: Double-click a task or select and click Edit
* Complete: Check the Completed checkbox in the task list
* Delete: Select a task and click Delete
* Filter: Use the search box and priority dropdown to filter tasks

### Schedule View

* Configure how many weeks to display (1-12)
* See all tasks organized by day
* Color-coded priorities and categories for quick visual reference


## Technical Details

### Architecture

* Model-View-Controller (MVC) pattern separation
* Swing with FlatLaf for modern UI components
* GSON for JSON serialization
* Java 17 features and APIs

### UI Components

* Custom table cell renderers for:
* Priority (color-coded)
* Category (color-coded)
* Schedule view (multi-line task display)
* Modern FlatLaf theming with custom overrides
* Responsive layout that works across different screen sizes

### Data Storage

All application data is stored in JSON format in the following files:

    tasks.json: Contains both active and completed tasks
    categories.json: Stores category definitions

These files are created automatically in the application's working directory.

### Key Dependencies

* FlatLaf (for modern Swing look and feel)
* GSON (for JSON serialization)
* JavaX Swing (for GUI components)


## Project Structure
```bash
src/
├── main/
│   ├── java/com/scheduler/
│   │   ├── models/          # Data models (Task, Category, Priority, etc.)
│   │   ├── utils/           # Utility classes (Storage, Date, etc.)
│   │   ├── views/           # GUI components
│   │   │   ├── components/  # Custom UI components (renderers, etc.)
│   │   │   ├── dialogs/     # Dialog windows
│   │   │   └── *.java       # Main view classes
│   │   └── AppMain.java     # Application entry point
│   └── resources/           # Resource files
├── test/                    # Unit tests
└── pom.xml                  # Maven configuration
```
## Screenshots
![](/Users/danielmoshood/Desktop/Screenshot 2025-07-18 at 00.17.35.png)
![](/Users/danielmoshood/Desktop/Screenshot 2025-07-18 at 00.16.45.png)
* Main task view with color-coded priorities and categories
* Calendar view showing tasks
* Schedule view with weekly overview
* Task creation dialog
* Recurrence settings dialog

## Contributing

_Contributions are welcome! Please follow these guidelines:_

* Fork the repository
* Create a feature branch (git checkout -b feature/your-feature)
* Commit your changes (git commit -am 'Add some feature')
* Push to the branch (git push origin feature/your-feature)
* Open a Pull Request

### Development Setup

* Import the project as a Maven project in your IDE
* Ensure Java 17 (or higher) SDK  is configured
* Run AppMain.java to start the application


_Smart Scheduler Pro - Organize your life, one task at a time! 🚀_
