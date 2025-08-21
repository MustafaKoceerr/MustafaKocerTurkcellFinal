<!-- README.md (NexusCart) -->

# NexusCart — Modern E-Commerce App

Hybrid Architecture · Clean MVVM + MVI · Secure by Design

---

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white) ![Architecture](https://img.shields.io/badge/Clean%20Architecture-Hybrid%20MVVM%2FMVI-4CAF50) ![Hilt](https://img.shields.io/badge/Hilt-2.56.2-1976D2?logo=dagger)

## App Gallery

> Screenshots will be added here (light and dark themes).

---

## Overview

NexusCart is a **modern, production-grade e-commerce application** showcasing:
- Hybrid **MVVM (XML)** + **MVI (Compose)**
- Secure token management
- Offline-first product browsing
- Reactive search, cart, orders, profile
- Built entirely with Kotlin, Coroutines/Flow, Hilt, Retrofit, Room, Paging 3, DataStore.

---

## Features

### 👤 Authentication
- Login & session persistence
- Secure token encryption (AES/GCM via AndroidKeyStore)
- Automatic auth header injection (`@Authenticated`)

### 🛍 Product Catalog
- Infinite scroll with Paging 3 + Room RemoteMediator
- Offline-first caching strategy
- Category feeds & category-scoped pagination
- Product detail: images, pricing, chips, reviews

### 🔍 Search
- Reactive search with debounce + validation
- Network-backed PagingSource

### 🛒 Cart & Orders
- Add, remove, increase/decrease quantity
- Clear cart instantly
- Orders list and order details with Room persistence

### 👤 Profile
- View & update profile with field validation
- Secure persistence of user data

### 🎨 Theming & UX
- Light/Dark theme support
- Material 3 + XML Material Components
- Animations (expand/collapse, back-press double exit)

---

## Architectural Highlights

- **Hybrid Architecture**: MVVM for XML Fragments, MVI for Compose screens.
- **Dependency Injection**: Hilt modules for API, DB, repositories, interceptors.
- **Centralized UseCases**: `LoginUseCase`, `SearchProductsUseCase`, `UpdateUserProfileUseCase`, etc.
- **Error Handling**: `AppException` + `ErrorMapperImpl` + `safeApiCall`.
- **Reusable UI State**: `StateLayout` for XML; `UiContract` for Compose.
- **Offline-First**: Room DB as single source of truth with RemoteMediator.

---

## Technologies & Libraries

- Kotlin 2.1.0
- Hilt 2.56.2
- Coroutines 1.10.2
- Retrofit 3.0.0 + Kotlinx Serialization
- OkHttp 4.12.0
- Room 2.7.2
- Paging 3.3.6
- DataStore 1.1.7
- Security Crypto 1.1.0
- Jetpack Compose BOM 2025.07.00 (Material3, activity-compose)
- Navigation Compose 2.9.3 + Fragment KTX
- Material Components 1.12.0
- Firebase (Messaging, Database, Config)

---

## Project Structure

```text
app/src/main/java/com/example/mustafakocer/
├─ data/                # DTOs, entities, network, db, prefs, repos
├─ domain/              # Models, use cases, repos contracts, exceptions
├─ presentation/        # Fragments (MVVM XML), Compose (MVI)
│  ├─ feature_auth/     # Compose login (MVI)
│  ├─ feature_cart/     # Cart (XML MVVM)
│  ├─ feature_orders/   # Orders (XML MVVM)
│  ├─ feature_profile/  # Profile (XML MVVM)
│  ├─ mvi/              # Base MVI contracts
│  └─ common/           # StateLayout, adapters, Compose components
├─ di/                  # Hilt modules
└─ util/                # Constants, helpers
```

---

## Setup & Build

1. Install Android Studio (Giraffe+), JDK 17
2. Clone repository
3. Sync Gradle
4. Run on device/emulator (min SDK 24, target 35)

Build variants: `debug`, `release`

```bash
./gradlew clean assembleDebug
```

---

## Running the App

- Entry activity (Compose Auth flow): `AuthActivity`
- After login → `MainActivity` (XML stack)
- Back-press on home: double-tap exit

---

## Security

- AES/GCM encryption via `CryptoManager`
- Secure DataStore storage
- Automated `Bearer` header injection via OkHttp interceptor

---

## Testing

- Unit tests: use cases, repositories
- ViewModel tests: state, events, effects
- Manual UI validation for StateLayout

---

## Roadmap

- Expand Compose coverage (orders, cart)
- Add instrumentation tests
- Enhance offline caching

---

## License

Educational project. Patterns may be reused with attribution.

---

➡ For in-depth explanation, see [`DETAILS.md`](./DETAILS.md).
