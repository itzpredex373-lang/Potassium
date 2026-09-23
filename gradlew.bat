@echo off
setlocal
set "APP_HOME=%~dp0"
set "GRADLE_VERSION=2.14.1"
set "GRADLE_DIR=%APP_HOME%.gradle-bootstrap"
set "GRADLE_HOME=%GRADLE_DIR%\gradle-%GRADLE_VERSION%"
set "GRADLE_ZIP=%GRADLE_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
set "GRADLE_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"

if exist "%GRADLE_HOME%\bin\gradle.bat" goto run

where java >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java is required. Use JDK 8.
    exit /b 1
)
if not exist "%GRADLE_DIR%" mkdir "%GRADLE_DIR%"
if not exist "%GRADLE_ZIP%" (
    echo Downloading Gradle %GRADLE_VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%GRADLE_URL%' -OutFile '%GRADLE_ZIP%'"
    if errorlevel 1 exit /b 1
)
if exist "%GRADLE_HOME%" rmdir /s /q "%GRADLE_HOME%"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%GRADLE_ZIP%' '%GRADLE_DIR%'"
if errorlevel 1 exit /b 1

:run
call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %errorlevel%
