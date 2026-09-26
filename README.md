# GroundWatch 

## Location-Based Sports Facility Condition Reporting and Maintenance Tracking System

GroundWatch is an Android application designed to help users report and track problems related to sports facilities.

Sports grounds and facilities can develop problems such as damaged playing surfaces, broken goalposts or nets, poor lighting, waterlogging, damaged seating, and other infrastructure issues. If these problems are not reported and tracked properly, they can affect the usability and safety of sports facilities.

GroundWatch provides a simple platform where users can report facility-related problems and help maintain better sports infrastructure.

---

## Problem Statement

Sports facilities require regular monitoring and maintenance. However, problems at sports grounds may not always be reported quickly or recorded properly.

Common problems include:

- Damaged playing surfaces
- Broken goalposts or nets
- Damaged sports equipment
- Poor lighting
- Waterlogging or drainage problems
- Damaged seating
- Other infrastructure-related issues

GroundWatch aims to provide a structured way to report these problems, associate them with a sports facility and location, and track their status.

---

## Proposed Solution

GroundWatch is an Android-based application that allows users to:

- View sports facilities
- Report problems related to a facility
- Add information about the reported problem
- Add images of the problem
- Record the location of the problem
- Track previously submitted reports
- Manage user information

The application is designed using concepts covered in the Mobile Application Development course.

---

## Main Features

### 1. User Registration and Login

Users can create an account and log in to the application.

The application provides:

- User registration
- Email and password input
- Password confirmation
- Login validation

### 2. Sports Facility Management

Users can view available sports facilities and their relevant information.

Facility information can include:

- Facility name
- Sports type
- Location
- Available facilities
- Condition information

### 3. Problem Reporting

Users can report problems found at sports facilities.

A report can contain:

- Facility information
- Problem type
- Description
- Date
- Image
- Location

### 4. Location Tracking

The application can use location information to identify where a sports facility or reported problem is located.

Google Maps and location tracking concepts are used as part of the planned implementation.

### 5. Image Handling

Users can add images related to reported facility problems.

For example:

- Damaged ground surface
- Broken equipment
- Damaged seating
- Lighting problems
- Waterlogging

### 6. Report Tracking

Users can view their previously submitted reports and track their status.

Possible report statuses include:

- Reported
- Under Review
- Maintenance in Progress
- Resolved

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application programming |
| Android Studio | Application development |
| XML | User interface design |
| Android SDK | Android application development |
| SQLite3 | Local database processing |
| Firebase | Database connectivity |
| Google Maps | Location tracking |
| Gradle | Project build system |

### Android Configuration

- Minimum SDK: 24
- Target SDK: 36
- Compile SDK: 36
- Java Version: 11

---

## Mobile Application Development Concepts Used

The project is developed according to the concepts included in the Mobile Application Development syllabus.

The application uses or plans to use:

- Android Activities
- Activity lifecycle
- Intents
- Screen layouts
- Buttons
- Text input controls
- Check Boxes
- Radio Buttons
- Spinner
- Date Picker
- Touch Listener
- Multiple screens
- Popup Dialogs
- Toast messages
- Menus
- Image display
- Image and file handling
- Text and XML file handling
- Location tracking
- Google Maps
- SQLite3
- Firebase
- Android application publishing

---

## Application Flow

```text
Splash Screen
      ↓
Login Screen
      ↓
Home Screen
      ↓
 ┌────┼──────────────┬─────────────┐
 ↓    ↓              ↓             ↓
Facilities   Report Problem    My Reports   Profile
```


## PROJECT STRUCTURE 
```
GroundWatch/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/groundwatch/
│           │       ├── SplashActivity.java
│           │       ├── MainActivity.java
│           │       ├── RegisterActivity.java
│           │       └── HomeActivity.java
│           │
│           ├── res/
│           │   ├── layout/
│           │   ├── drawable/
│           │   ├── mipmap/
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
