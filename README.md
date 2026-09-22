# CareerLaunch SA

We built this app to solve a problem I kept running into: finding IT internships and graduate programmes in South Africa usually means scrolling through a dozen different websites and WhatsApp groups. CareerLaunch SA puts them all in one place.

It is an Android app where students and recent graduates can browse IT opportunities, apply, upload their CV and keep track of where they applied.

## Screenshots

|<img width="727" height="1600" alt="WhatsApp Image 2026-09-22 at 7 33 30 PM" src="https://github.com/user-attachments/assets/f0b164db-2c2d-4884-9d6e-359b055a8eda" />
<img width="727" height="1600" alt="WhatsApp Image 2026-09-22 at 7 33 29 PM" src="https://github.com/user-attachments/assets/513b66b5-c0e8-48e6-8e30-9dfc34f50406" />
<img width="727" height="1600" alt="WhatsApp Image 2026-09-22 at 7 33 29 PM (1)" src="https://github.com/user-attachments/assets/ef3a5556-12c6-4d67-8f51-f37ed29875f4" />
<img width="1080" height="2376" alt="WhatsApp Image 2026-09-22 at 7 33 29 PM (2)" src="https://github.com/user-attachments/assets/85ae2ed1-8da4-40c0-ae9f-75260b5ed39c" />
<img width="727" height="1600" alt="WhatsApp Image 2026-09-22 at 7 33 28 PM" src="https://github.com/user-attachments/assets/aa0a59a9-b790-4aa5-85f8-12742a08ba35" />


## What the app does

- Browse a list of internships and graduate programmes from companies like Standard Bank, Vodacom and Capitec
- Search by keyword (Kotlin, SQL, Cloud, Web) or filter by category like Software Dev or Data and SQL
- Tap "Apply Now" to apply straight from the app, or "Save Offline" to keep a job for later
- Upload your CV once (PDF or DOCX, max 5 MB) so applying is quicker
- Track every application and see its status: Applied, Interview, Offer or Unsuccessful
- Get job alerts and notifications when your application status changes
- Change your password, log out of all devices, manage offline sync and check data usage in Settings

## How we built it

| Part | What we used |
| --- | --- |
| Language | Kotlin |
| Platform | Android |
| UI | XML layouts with Material Components |
| Local storage | Room Database for offline caching |
| Networking | Retrofit to talk to the REST API |
| Notifications | Firebase Cloud Messaging |
| Authentication | Firebase Auth |

## Running the app

You will need Android Studio and a device or emulator running Android 8.0 (API 26) or higher.

```bash
# 1. Clone the repo
git clone https://github.com/your-username/careerlaunch-sa.git

# 2. Open the project in Android Studio

# 3. Add your own config:
#    - google-services.json from Firebase, in the app/ folder
#    - API base URL in local.properties or BuildConfig

# 4. Build and run
./gradlew build
./gradlew installDebug
```

The base URL goes in `local.properties`:

```properties
API_BASE_URL=https://api.careerlaunchsa.co.za/
```

## How to use it

1. Register with your email address
2. Upload your CV under My CV
3. Browse or search for jobs on the Browse tab
4. Tap Apply Now, or Save Offline to look at it later
5. Check the Applications tab to see how your applications are progressing
6. Turn on Job Alerts and Application Status notifications in Settings

## Project structure

```javascript
com.careerlaunchsa.app
├── ui/
│   ├── auth/          # login and registration
│   ├── browse/        # job listings, search and filters
│   ├── applications/  # tracking applications
│   ├── cv/            # CV upload
│   └── settings/      # security, notifications and sync
├── data/
│   ├── local/         # Room DB for offline cache
│   ├── remote/        # Retrofit API service
│   └── repository/    # data repositories
├── notifications/     # push notifications
└── utils/             # helpers
```

## What we would add next

- Interview scheduling inside the app
- CV parsing that fills in your profile automatically
- A portal for employers to post their own jobs
- A dashboard showing your application stats
- Dark mode

YOUTUBE LINK
https://youtu.be/wJCTXsInDRU?si=6_tsBY5zUvnTOy3X
