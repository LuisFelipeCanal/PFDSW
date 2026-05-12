# MercadoVIVO

Proyecto Android en Kotlin (Jetpack Compose) para el curso de DSW.

## Estructura esencial del proyecto

- `app/`: codigo Kotlin, pantallas Compose, recursos (`res/`) y pruebas.
- `gradle/` + `gradlew`/`gradlew.bat`: wrapper de Gradle para compilar en cualquier equipo.
- `build.gradle.kts` + `settings.gradle.kts` + `gradle.properties`: configuracion de build.

## Package y configuracion principal

- Package base: `com.mercadovivo.app`
- `namespace` y `applicationId` alineados en `app/build.gradle.kts`.
- Firebase configurado con `app/google-services.json`.

## Comandos utiles

```powershell
.\gradlew.bat :app:assembleDebug --no-daemon
.\gradlew.bat :app:testDebugUnitTest --no-daemon
```

## Nota de limpieza para GitHub

Este repositorio ignora archivos generados/locales (`build/`, `.gradle/`, `.idea/`, `.kotlin/`) para mantener visible solo el codigo esencial.

