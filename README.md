# MercadoVIVO

This project implements a hybrid cloud architecture for Android using Jetpack Compose, integrating Firebase (Auth, Firestore, Storage) for real-time data management and the Dropbox API for distributed high-performance video streaming. It features a professional recipe player with intelligent aspect-ratio detection and custom media controls, optimized specifically for low-resource environments like the Honor x6c through real-time image compression and asynchronous coroutine handling.

## Estructura esencial del proyecto

- `app/`: codigo Kotlin, pantallas Compose, recursos (`res/`) y pruebas.
- `gradle/` + `gradlew`/`gradlew.bat`: wrapper de Gradle para compilar en cualquier equipo.
- `build.gradle.kts` + `settings.gradle.kts` + `gradle.properties`: configuracion de build.

## Package y configuracion principal

- Package base: `com.mercadovivo.app`
- `namespace` y `applicationId` alineados en `app/build.gradle.kts`.
- Firebase configurado con `app/google-services.json`.

## Versión 2.0 - Integración Dropbox & Optimización

Esta versión incluye integración híbrida con **Dropbox API** para la gestión de videos de preparación y optimizaciones específicas para dispositivos de recursos limitados como el **Honor x6c**.

### Configuración obligatoria (Post-clonado)

Para que el proyecto compile y funcione correctamente, debes configurar los secretos locales que no se suben a GitHub:

1. Crea o abre el archivo `local.properties` en la raíz del proyecto.
2. Añade las siguientes claves de Dropbox (Solicítalas al administrador):
   ```properties
   DROPBOX_APP_KEY=tu_app_key_aqui
   DROPBOX_APP_SECRET=tu_app_secret_aqui
   DROPBOX_REFRESH_TOKEN=tu_refresh_token_aqui
   ```
3. Asegúrate de tener el archivo `app/google-services.json` de tu proyecto Firebase.
4. Realiza un **Gradle Sync** en Android Studio.

## Comandos utiles

```powershell
.\gradlew.bat :app:assembleDebug --no-daemon
.\gradlew.bat :app:testDebugUnitTest --no-daemon
```

## Nota de limpieza para GitHub

Este repositorio ignora archivos generados/locales (`build/`, `.gradle/`, `.idea/`, `.kotlin/`) para mantener visible solo el codigo esencial.

