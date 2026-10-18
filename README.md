# GroundWatch

### Sports Facility Reporting and Maintenance Tracking

GroundWatch is a Java Android application for reporting problems at sports facilities and managing their maintenance. Users browse facilities and submit issues; administrators review submissions, assign severity, and update maintenance status.

**Project status:** Local prototype developed for a Mobile Application Development project. Accounts, reports, and facility settings are stored in SQLite on the device.

## Features

### User dashboard

- Register and log in with email and password.
- View the venue name, Public/Private type, and address.
- Browse facility cards showing sport and stored availability.
- Open facility details and start a report with that facility selected.
- Submit a problem category and description; the submission date is recorded automatically.
- Open the venue address in Google Maps or a browser.
- View profile details and log out.

### Admin dashboard

- Access a separate dashboard according to the account's database role.
- Review reports submitted by all users on the same installation.
- Assign Low, Medium, or High severity.
- Update maintenance status and associated dates.
- View all submissions through **My Reports**.
- Edit the venue name, Public/Private type, and address displayed on both dashboards.
- Open the venue map, view profile details, and log out.

Normal registration creates a `user` account. Users do not select severity and do not have My Reports or maintenance-management buttons on their dashboard. New reports start with severity `Unassigned`.

## Roles and Workflow

| Action | User | Admin |
|---|---|---|
| Browse facilities and submit problems | User dashboard | Review submitted problems |
| View venue details | Yes | Yes |
| Change venue details | No | Yes |
| View all submitted reports | No | Yes |
| Assign severity | No | Yes |
| Update maintenance status | No | Yes |
| Profile and logout | Yes | Yes |

A typical workflow is:

1. A user selects a sports facility and submits a problem.
2. The report is saved as `Reported` with severity `Unassigned`.
3. An admin reviews it and assigns severity.
4. The admin sets maintenance status to `Pending`, `In Progress`, or `Completed`.
5. The updated report is available in the admin report lists.

Status changes are selected manually; the application does not require every intermediate status. Moving a task to Pending clears its work dates. Reopening it as In Progress clears its completion date.

Facility availability and report maintenance status are separate fields. Updating a report does not automatically change facility availability.

## Default Facilities and Categories

| Facility | Sport |
|---|---|
| Cricket Ground | Cricket |
| Football Ground | Football |
| Basketball Court | Basketball |
| Badminton Court | Badminton |

Problem categories: Damaged Surface, Broken Equipment, Poor Lighting, Water Logging, Cleanliness Issue, and Other.

The initial venue is **NMIMS Shirpur Sports Facility**, with type **Private**. An administrator must enter the actual address. Venue settings are shared by the facilities in the current prototype.

## Technology and Configuration

| Component | Current implementation |
|---|---|
| Language | Java |
| UI | XML layouts and Material Components |
| Navigation | Android Activities and Intents |
| Persistence | SQLite through SQLiteOpenHelper |
| Session information | SharedPreferences |
| Maps | External Google Maps search URL with browser fallback |
| Minimum Android version | API 24 |
| Target SDK | API 36 |
| Compile SDK | API 36.1 |
| Java source compatibility | Java 11 |
| Gradle daemon toolchain | Java 21 |
| Android Gradle Plugin | 9.2.1 |
| Gradle wrapper | 9.5.0 |
| Database | GroundWatch.db, version 3 |

The map link uses the saved venue name and address. It does not require a Maps API key, collect GPS coordinates, or embed a map inside GroundWatch.

## Getting Started

### Prerequisites

- Android Studio supporting Android Gradle Plugin 9.2.1.
- Android SDK Platform 36.1 and the build tools requested during sync.
- A compatible Java 21 Gradle runtime; Java 11 above refers to source compatibility.
- An emulator or Android device running API 24 or newer.
- Internet access for initial dependency downloads and online map searches.

### Clone and run

```bash
git clone https://github.com/neetgupta15/GroundWatch-Android.git
```

1. Open Android Studio and select **Open**.
2. Select the cloned `GroundWatch-Android` directory.
3. Allow Gradle sync to finish and install requested SDK components.
4. Select the **app** run configuration.
5. Start an emulator or connect a device with USB debugging enabled.
6. Click **Run** and register a user account.

## Setting Up an Admin

There is no built-in admin email or password. Each app installation needs its own admin account.

1. Register an account in the app using your chosen email and password.
2. Keep the debug app running on an emulator/device with API 26 or newer.
3. Open **View > Tool Windows > App Inspection > Database Inspector**.
4. Select the GroundWatch process and `GroundWatch.db`.
5. Enable **Keep database connections open** if needed.
6. Run the query below, replacing the email with the account you registered:

```sql
UPDATE Users
SET role = 'admin'
WHERE email = 'your-registered-email@example.com';
```

Verify the account:

```sql
SELECT user_id, name, email, role
FROM Users;
```

Log out and log in again using the registered email and password. The role query does not create an account or change its password.

On the admin dashboard, select **Edit Facility and Address** and save the correct venue details. Database Inspector provisioning is a development workflow for this local prototype.

## Database

| Table | Purpose |
|---|---|
| Users | Accounts and user/admin roles |
| Facilities | Facility names, sports, and availability |
| Reports | Reporter, facility, category, description, severity, status, and date |
| Maintenance | Report-linked work status and dates |
| Site | Shared venue name, Public/Private type, and address |

Version 2 introduced venue settings. Version 3 repairs missing default facilities. Upgrades preserve existing records; do not uninstall the app or clear storage when testing migrations.

## Source Guide

Java sources are under `app/src/main/java/com/example/groundwatch/`.

| File | Responsibility |
|---|---|
| SplashActivity.java | Startup screen |
| MainActivity.java | Login and role-based routing |
| RegisterActivity.java | User registration |
| HomeActivity.java | User dashboard |
| activity_admin_home.java | Admin dashboard and venue editing |
| FacilitiesActivity.java | Database-backed facility cards and details |
| ReportProblemActivity.java | Problem submission |
| MaintenanceActivity.java | Admin severity and maintenance controls |
| MyReportsActivity.java | Admin list of all submissions |
| ProfileActivity.java | Account details and logout |
| Session.java | Login and role checks |
| MapsHelper.java | External venue map link |
| DBHelper.java | Schema, migrations, queries, and updates |

Screen layouts are under `app/src/main/res/layout/`. `item_facility.xml` is the reusable facility card layout.

## Manual Verification

- Register a user; verify correct and incorrect login attempts.
- Confirm user dashboards have no severity selector, My Reports, or maintenance-management controls.
- Browse facility cards and verify selection carries into the report form.
- Submit a report and confirm the admin sees it with Unassigned severity.
- Assign severity, update status, and confirm both persist after restarting.
- Change Completed to Pending; verify start and completion dates are cleared.
- Edit venue details and check both dashboards on the same installation.
- Open Maps; verify the saved address and browser fallback where available.
- Test an upgrade without clearing data; confirm accounts and reports remain.

Command-line debug build:

```bash
# macOS/Linux
./gradlew assembleDebug
```

```powershell
# Windows
.\gradlew.bat assembleDebug
```

## Troubleshooting

**Incompatible Android Gradle Plugin:** Check Android Studio compatibility with AGP 9.2.1. Its version is defined in `gradle/libs.versions.toml`.

**No Databases Available in Inspector:** Run the app, select the current GroundWatch process, open the login screen, and enable Keep database connections open.

**Admin login fails:** Confirm the account exists in Users and has role `admin`. Promoting an email that has not been registered updates no rows.

**Cannot find `R.layout.item_facility`:** Create `app/src/main/res/layout/item_facility.xml` with the card's expected view IDs.

**Cannot find `R.layout.activity_item_facility`:** The current repository includes an accidental `item_facility.java` activity referencing this missing layout. Remove that unused Java file; facility rows use `item_facility.xml` and do not need a separate Activity.

**Map does not open:** Save a complete venue address first. Install Google Maps or a browser and check connectivity.

## Current Limitations

- SQLite data stays on one installation. User and admin accounts must use the same installation to share reports.
- GitHub stores source code, not emulator accounts, reports, or configured venue details.
- Passwords currently use plaintext local storage; password hashing and account migration remain pending.
- Report image attachment, GPS capture, embedded maps, Firebase synchronization, and notifications are not implemented.
- Venue details are global settings, not independently stored addresses for every facility.
- Admin roles currently require development-time setup through Database Inspector.

## Planned Improvements

- Salted password hashing and safer account management.
- Shared backend authentication, authorization, and report synchronization.
- Admin controls for adding and editing facilities.
- Report photo attachments and facility-specific coordinates.
- Embedded maps and optional GPS-based reporting.
- Search, filtering, maintenance assignment, and notifications.
- Automated tests for authentication, migrations, and reporting.
