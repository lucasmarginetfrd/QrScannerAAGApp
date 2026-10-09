🏌️ AAG — 119th VISA Open | Ticket Validation App

<p align="center"> <img src="docs/images/aag-logo.png" alt="Argentine Golf Association" width="140" /> </p>

<h3 align="center">A mobile ticket validation system developed for the 119th VISA Open</h3>

<p align="center"> <strong>Android · Kotlin · Jetpack Compose · QR Code Scanning · REST API · PHP · SQL · Firebase</strong> </p>

<p align="center"> A dedicated Android application designed to streamline event access control by scanning QR codes, retrieving ticket holder information, and synchronizing admission records across backend and cloud databases. </p>

📱 Overview

The AAG Ticket Validation App is an Android application developed in Kotlin to support the ticket admission process for the 119th VISA Open, associated with the Argentine Golf Association (Asociación Argentina de Golf — AAG).

The application uses the device's camera to scan QR codes associated with purchased tickets. Once a code is scanned, the app retrieves the corresponding ticket holder's information through a backend integration, allowing event staff to identify the person associated with the ticket and process their admission.

The admission information is then transmitted to a PHP-based backend connected to an SQL database and synchronized with Firebase, providing multiple data destinations for recording and managing entry information.

The application also includes a custom-designed home screen inspired by the visual identity of the Argentine Golf Association, featuring its characteristic dark green palette and a clean, event-focused interface.

✨ Key Features
QR Code Scanning: Uses the device camera to scan ticket QR codes at the event entrance.
Ticket Holder Identification: Retrieves the information associated with a scanned ticket through the backend API.
Admission Registration: Sends entry records to the backend when an admission is processed.
PHP REST API Integration: Communicates with the server to retrieve ticket information and transmit admission data.
SQL Database Integration: Stores admission records and related information through the PHP backend.
Firebase Integration: Sends admission information to Firebase as an additional data destination.
Custom Home Screen: Presents a branded landing screen inspired by the Argentine Golf Association and the 119th VISA Open.
Dependency Injection: Uses Hilt to organize dependency management and improve component separation.
Modern Android UI: Uses Jetpack Compose alongside XML-based Android resources where applicable.
Local Data Persistence: Uses Room for structured local storage on the device.
🖼️ Application Preview

<p align="center"> <img src="docs/images/home-screen.png" alt="AAG Ticket Validation App home screen for the 119th VISA Open" width="300" /> </p>

<p align="center"> <em>Home screen — 119th VISA Open, Argentine Golf Association.</em> </p>

Setup note: Save your home-screen screenshot as docs/images/home-screen.png inside the repository. You can also add additional screenshots of the QR scanner and ticket holder information screens to showcase the complete workflow.
🔄 How It Works

The application follows a ticket validation workflow designed to connect the mobile scanning experience with the backend infrastructure.

Launch the application: The user opens the app and is presented with the AAG-branded home screen.
Scan the QR code: The device camera captures the QR code associated with the ticket.
Retrieve ticket information: The application processes the scanned code and communicates with the REST API to retrieve the ticket holder's information.
Process admission: The retrieved information is used to identify the person associated with the ticket and support the admission process.
Register the entry: Admission information is transmitted to the PHP backend and its SQL database.
Synchronize with Firebase: The application also sends admission information to Firebase, providing an additional destination for entry records.
System Workflow
        ANDROID APPLICATION
        Kotlin + Jetpack Compose
                  |
                  v
           Device Camera
                  |
                  v
           QR Code Scanner
                  |
                  v
        Scanned Ticket Data
                  |
                  v
            REST API
                  |
                  v
           PHP Backend
                  |
                  v
             SQL Database

        Admission Information
                  |
                  v
              Firebase


The diagram represents the main logical flow. The exact request sequence and synchronization behavior depend on the application's implementation.

🛠️ Technology Stack
Android Development
Technology	Purpose
Kotlin	Primary programming language for Android development.
Android SDK	Native Android platform APIs and application integration.
Jetpack Compose	Declarative UI development for Android screens.
XML	Android resources and XML-based configurations, where applicable.
Camera APIs	Access to the device camera for QR code scanning.
Hilt	Dependency injection and dependency management.
Room	Local database persistence and structured data access.
Gradle	Build configuration and dependency management.
Backend & Data Management
Technology	Purpose
REST API	Communication between the Android app and the backend services.
PHP	Server-side processing and database integration.
SQL	Relational database storage for ticket and admission information.
Firebase	Additional cloud-based destination for admission records.
JSON / HTTP	Data exchange between the mobile application and REST endpoints, where applicable.
🏗️ Technical Highlights
Native Android Development

Built with Kotlin, the application integrates Android platform capabilities with a modern user interface. Jetpack Compose provides a declarative approach to screen development, while XML resources support the Android configuration and resource system where needed.

Camera-Based QR Code Processing

The camera integration enables event staff to scan ticket QR codes directly from an Android device, reducing the need for manual ticket identification.

The scanned information serves as the input for the ticket lookup and admission workflow.

Backend Communication

The application communicates with a PHP-powered REST API to retrieve ticket holder information and transmit admission records.

This separation allows the mobile interface to interact with backend services without directly depending on the underlying SQL database.

Multi-Database Integration

The system integrates different data technologies for distinct purposes:

Room: Local persistence within the Android application.
SQL: Server-side relational data storage accessed through the PHP backend.
Firebase: An additional destination for admission information.

Using these components demonstrates experience working with local storage, server-side databases, and cloud services in a single mobile application.

Dependency Injection

Hilt provides dependency injection capabilities, helping organize the application's dependencies and supporting a more maintainable codebase.

🧩 Core Components

The application can be understood through the following functional components:

Component	Responsibility
Home Screen	Displays the event identity and provides the starting point for the admission workflow.
QR Scanner	Uses the device camera to capture ticket QR codes.
Ticket Lookup	Requests information associated with the scanned ticket.
Admission Processing	Handles the information needed to register a person's entry.
REST API Client	Communicates with the PHP backend.
SQL Integration	Supports server-side storage through the backend.
Firebase Integration	Sends admission information to Firebase.
Room Database	Provides local persistence on the device.
🔐 Data Handling & Reliability

Because the application is designed for event access control, reliable data handling is an important part of the system.

Recommended considerations for production deployments include:

Validate ticket eligibility and admission status on the server.
Prevent duplicate admissions through server-side checks and appropriate database constraints.
Handle network errors and unsuccessful API responses gracefully.
Protect API communication with HTTPS.
Avoid exposing credentials, private API keys, or sensitive configuration values in the Android source code.
Define clear synchronization and retry behavior when backend or Firebase requests fail.
Request only the personal information required for the admission process.

These considerations are especially important when multiple data destinations are involved and admission records must remain consistent.

🚀 Getting Started
Prerequisites

To build and run the Android application, you will generally need:

Android Studio.
A compatible Android SDK.
A Kotlin-compatible Android development environment.
Access to the PHP backend and its configured SQL database.
Firebase project configuration, if required by the application.
An Android device or emulator with camera support for scanning tests.
Installation
Clone the repository:
   git clone <YOUR_REPOSITORY_URL>

Open the project in Android Studio.
Allow Gradle to synchronize the project and download the required dependencies.
Configure the backend API endpoint and the required Firebase settings using your project's configuration approach.
Ensure the PHP backend and SQL database are available and correctly configured.
Connect an Android device or start an emulator.
Build and run the application from Android Studio.
Note: Replace <YOUR_REPOSITORY_URL> with the actual GitHub repository URL. The backend setup, Firebase configuration, and any required environment variables depend on the project configuration.
🧪 Testing Considerations

The following scenarios are useful when validating the application:

Scanning a valid ticket QR code.
Scanning an invalid or unreadable QR code.
Retrieving ticket holder information successfully.
Handling tickets that cannot be found by the backend.
Registering an admission successfully.
Handling repeated scans and duplicate admission attempts.
Testing behavior when the network is unavailable.
Verifying that admission records reach the intended SQL and Firebase destinations.
Confirming that camera permissions and error states are handled correctly.
🎯 Project Goals

The main goals of the application are to:

Digitize the ticket admission workflow for a major golf event.
Simplify ticket holder identification at the entrance.
Reduce manual data entry during admission.
Connect a native Android application with a PHP REST API.
Integrate relational database storage with Firebase.
Provide an event-specific interface aligned with the AAG visual identity.
Apply modern Android development technologies to a practical event management use case.
📚 Skills Demonstrated

This project showcases practical experience with:

Native Android application development using Kotlin.
Modern UI development with Jetpack Compose.
Camera integration and QR code scanning workflows.
RESTful API consumption.
PHP backend integration.
SQL database connectivity through server-side services.
Firebase integration.
Local persistence using Room.
Dependency injection with Hilt.
Android resource management and XML.
Cross-layer data flow between mobile, backend, relational database, and cloud services.
UI design and branding for a real-world event context.
🔮 Potential Improvements

Possible future enhancements include:

An explicit admission success or failure screen.
Offline admission queuing with automatic synchronization when connectivity returns.
Real-time duplicate ticket detection.
A scan history and admission statistics dashboard.
Improved error reporting and diagnostic logging.
Automated unit and integration tests.
A dedicated staff authentication and authorization workflow.
Monitoring for failed synchronization between data destinations.
👨‍💻 About

Developed as an Android application for ticket scanning and admission registration associated with the 119th VISA Open, incorporating the visual identity of the Argentine Golf Association (AAG).

The project brings together mobile development, QR code scanning, backend communication, and multi-destination data integration in a practical event access control solution.

<p align="center"> <strong>Built with Kotlin and Android technologies 🏌️</strong> </p>

<p align="center"> <em>Argentine Golf Association · 119th VISA Open</em> </p>
